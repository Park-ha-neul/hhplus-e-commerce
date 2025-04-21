package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TopProductTest {

    @Mock
    private TopProductRepository topProductRepository;

    @Test
    void 정상_생성(){
        Long productId = 1L;
        TopProduct.PeriodType periodType = TopProduct.PeriodType.DAILY;
        LocalDate date = LocalDate.of(2025, 4, 11);
        Long count = 5L;

        TopProduct topProduct = new TopProduct(productId,1, count, date, periodType);

        assertEquals(periodType, topProduct.getPeriodType());
        assertEquals(date, topProduct.getCalculateDate());
        assertEquals(count, topProduct.getTotalCount());
    }

    @Test
    void addCount_누적_성공() {
        Long productId = 1L;
        TopProduct.PeriodType periodType = TopProduct.PeriodType.DAILY;
        LocalDate date = LocalDate.of(2025, 4, 11);
        Long count = 5L;

        TopProduct topProduct = new TopProduct(productId,1, count, date, periodType);

        topProduct.addCount(5L);

        assertEquals(Long.valueOf(15L), topProduct.getTotalCount());
    }


    @Test
    void calculateCalculatedDate_기간별_정상계산() {
        assertEquals(LocalDate.now(), TopProduct.calculateDate(TopProduct.PeriodType.DAILY));
        assertEquals(LocalDate.now().with(DayOfWeek.MONDAY), TopProduct.calculateDate(TopProduct.PeriodType.WEEKLY));
        assertEquals(LocalDate.now().withDayOfMonth(1), TopProduct.calculateDate(TopProduct.PeriodType.MONTHLY));
    }
}
