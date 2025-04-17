package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopProductServiceTest {

    @InjectMocks
    private TopProductService topProductService;

    @Mock
    private TopProductEntityRepository topProductEntityRepository;

    @Test
    void 상위_상품_조회_성공() {
        // given
        PeriodType periodType = PeriodType.DAILY; // 예시로 DAILY로 설정
        LocalDate calculatedDate = LocalDate.now().minusDays(1); // 예시로 어제 날짜

        // TopProductEntity의 calculateDate 메서드를 mock
        try (MockedStatic<TopProductEntity> mockedStatic = mockStatic(TopProductEntity.class)) {
            mockedStatic.when(() -> TopProductEntity.calculateDate(periodType)).thenReturn(calculatedDate);

            // 상위 5개의 TopProductEntity를 반환하도록 설정
            TopProductEntity topProduct1 = mock(TopProductEntity.class);
            TopProductEntity topProduct2 = mock(TopProductEntity.class);
            List<TopProductEntity> topProducts = List.of(topProduct1, topProduct2);

            // repository에서 상위 상품을 조회하도록 mock
            when(topProductEntityRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculatedDate, PageRequest.of(0, 5)))
                    .thenReturn(topProducts);

            // when
            List<TopProductEntity> result = topProductService.getTopProductsByPeriod(periodType);

            // then
            assertNotNull(result);
            assertEquals(2, result.size()); // 2개의 상품이 반환됨
            verify(topProductEntityRepository).findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculatedDate, PageRequest.of(0, 5));
        }
    }
}
