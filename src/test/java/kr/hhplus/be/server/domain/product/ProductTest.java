package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProductTest {
    @Test
    public void 상품_판매가능_상태_변경(){
        Product product = new Product(1l, "상품 A", "좋은 상품", 2000L, new Balance(0L), ProductStatus.SOLD_OUT);
        product.updateStatus(ProductStatus.AVAILABLE);

        assertEquals(ProductStatus.AVAILABLE,product.getStatus());
    }

    @Test
    public void 상품_품절_상태_변경(){
        Product product = new Product(1l, "상품 A", "좋은 상품", 2000L, new Balance(2L), ProductStatus.AVAILABLE);
        product.updateStatus(ProductStatus.SOLD_OUT);

        assertEquals(ProductStatus.SOLD_OUT,product.getStatus());
    }
}
