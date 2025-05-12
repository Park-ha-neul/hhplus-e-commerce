package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PopularProductTest {

    @Test
    void 정상_생성(){
        Long productId = 1L;
        PopularProduct.PeriodType periodType = PopularProduct.PeriodType.DAILY;
        Long count = 5L;

        PopularProduct popularProduct = new PopularProduct(productId, "상품 1", 1000L, count, 1, periodType);

        assertEquals(periodType, popularProduct.getPeriodType());
        assertEquals(count, popularProduct.getTotalCount());
    }
}
