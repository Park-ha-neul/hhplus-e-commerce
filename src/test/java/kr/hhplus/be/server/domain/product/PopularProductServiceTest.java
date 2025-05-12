package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.product.PopularProductProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PopularProductServiceTest {

    @InjectMocks
    private PopularProductService popularProductService;

    @Mock
    private PopularProductRepository popularProductRepository;

    @DisplayName("상위 상품 조회시 오름차순 정렬됨")
    @Test
    void getPopularProducts_ascending_success() {
        // given
        PopularProduct.PeriodType periodType = PopularProduct.PeriodType.DAILY; // 예시로 DAILY로 설정
        LocalDate now = LocalDate.now();
        LocalDateTime startDate = periodType.getStartDate(now).atStartOfDay();
        LocalDateTime endDate = periodType.getEndDate(now).atTime(LocalTime.MAX);
        int limit = 5;
        boolean ascending = true;

        PopularProductProjection projection1 = mock(PopularProductProjection.class);
        when(projection1.getProductId()).thenReturn(1L);
        when(projection1.getProductName()).thenReturn("상품 A");
        when(projection1.getPrice()).thenReturn(1000L);
        when(projection1.getTotalCount()).thenReturn(100L);
        when(projection1.getRanking()).thenReturn(2);

        PopularProductProjection projection2 = mock(PopularProductProjection.class);
        when(projection2.getProductId()).thenReturn(2L);
        when(projection2.getProductName()).thenReturn("상품 B");
        when(projection2.getPrice()).thenReturn(1000L);
        when(projection2.getTotalCount()).thenReturn(200L);
        when(projection2.getRanking()).thenReturn(1);

        List<PopularProductProjection> projections = List.of(projection1, projection2);

        when(popularProductRepository.findByPeriodAndDateRange(startDate, endDate, limit))
                .thenReturn(projections);

        // when
        List<PopularProduct> result = popularProductService.getPopularProducts(periodType, ascending, limit);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRanking().intValue());
        assertEquals(2, result.get(1).getRanking().intValue());

        verify(popularProductRepository, times(1)).findByPeriodAndDateRange(startDate, endDate, limit);
    }

    @DisplayName("상위 상품 조회시 데이터가 없으면 빈 리스트 반환")
    @Test
    void getPopularProducts_emptyList_success() {
        // given
        PopularProduct.PeriodType periodType = PopularProduct.PeriodType.DAILY;
        LocalDate now = LocalDate.now();
        LocalDateTime startDate = periodType.getStartDate(now).atStartOfDay();
        LocalDateTime endDate = periodType.getEndDate(now).atTime(LocalTime.MAX);
        int limit = 5;

        boolean ascending = true;

        when(popularProductRepository.findByPeriodAndDateRange(startDate, endDate, limit))
                .thenReturn(Collections.emptyList());

        // when
        List<PopularProduct> result = popularProductService.getPopularProducts(periodType, ascending, limit);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(popularProductRepository, times(1)).findByPeriodAndDateRange(startDate, endDate, limit);
    }
}
