package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TopProductEntityTest {

    @Mock
    private TopProductEntityRepository topProductEntityRepository;

    @Test
    void 정상_생성(){
        ProductEntity mockProductEntity = mock(ProductEntity.class);
        PeriodType periodType = PeriodType.DAILY;
        LocalDate date = LocalDate.of(2025, 4, 11);
        Long count = 5L;

        TopProductEntity topProductEntity = TopProductEntity.create(mockProductEntity, periodType, date, count);

        assertEquals(mockProductEntity, topProductEntity.getProductEntity());
        assertEquals(periodType, topProductEntity.getPeriodType());
        assertEquals(date, topProductEntity.getCalculateDate());
        assertEquals(count, topProductEntity.getTotalCount());
    }

    @Test
    void addCount_누적_성공() {
        ProductEntity mockProductEntity = mock(ProductEntity.class);
        TopProductEntity topProductEntity = TopProductEntity.create(mockProductEntity, PeriodType.DAILY, LocalDate.now(), 10L);

        topProductEntity.addCount(5L);

        assertEquals(Long.valueOf(15L), topProductEntity.getTotalCount());
    }

    @Test
    void addCount_null초기값_처리() {
        ProductEntity mockProductEntity = mock(ProductEntity.class);
        TopProductEntity topProductEntity = TopProductEntity.create(mockProductEntity, PeriodType.DAILY, LocalDate.now(), null);

        topProductEntity.addCount(3L);

        assertEquals(Long.valueOf(3L), topProductEntity.getTotalCount());
    }

    @Test
    void calculateCalculatedDate_기간별_정상계산() {
        assertEquals(LocalDate.now(), TopProductEntity.calculateDate(PeriodType.DAILY));
        assertEquals(LocalDate.now().with(DayOfWeek.MONDAY), TopProductEntity.calculateDate(PeriodType.WEEKLY));
        assertEquals(LocalDate.now().withDayOfMonth(1), TopProductEntity.calculateDate(PeriodType.MONTHLY));
    }
}
