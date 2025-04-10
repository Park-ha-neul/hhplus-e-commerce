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
public class TopProductTest {

    @Mock
    private TopProductRepository topProductRepository;

    @Test
    void 정상_생성(){
        Product mockProduct = mock(Product.class);
        PeriodType periodType = PeriodType.DAILY;
        LocalDate date = LocalDate.of(2025, 4, 11);
        Long count = 5L;

        TopProduct topProduct = TopProduct.create(mockProduct, periodType, date, count);

        assertEquals(mockProduct, topProduct.getProduct());
        assertEquals(periodType, topProduct.getPeriodType());
        assertEquals(date, topProduct.getCalculateDate());
        assertEquals(count, topProduct.getTotalCount());
    }

    @Test
    void addCount_누적_성공() {
        Product mockProduct = mock(Product.class);
        TopProduct topProduct = TopProduct.create(mockProduct, PeriodType.DAILY, LocalDate.now(), 10L);

        topProduct.addCount(5L);

        assertEquals(Long.valueOf(15L), topProduct.getTotalCount());
    }

    @Test
    void addCount_null초기값_처리() {
        Product mockProduct = mock(Product.class);
        TopProduct topProduct = TopProduct.create(mockProduct, PeriodType.DAILY, LocalDate.now(), null);

        topProduct.addCount(3L);

        assertEquals(Long.valueOf(3L), topProduct.getTotalCount());
    }
    @Test
    void calculateCalculatedDate_기간별_정상계산() {
        assertEquals(LocalDate.now(), TopProduct.calculateCalculatedDate(PeriodType.DAILY));
        assertEquals(LocalDate.now().with(DayOfWeek.MONDAY), TopProduct.calculateCalculatedDate(PeriodType.WEEKLY));
        assertEquals(LocalDate.now().withDayOfMonth(1), TopProduct.calculateCalculatedDate(PeriodType.MONTHLY));
    }
}
