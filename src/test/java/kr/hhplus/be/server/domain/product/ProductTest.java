package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    @Test
    public void 상품_등록_성공(){
        Balance balance = Balance.create(100L);
        Product product = Product.create("A 상품", "좋은 상품", 1000L, balance, ProductStatus.AVAILABLE);

        assertEquals("A 상품", product.getName());
    }

    @Test
    public void 상품_등록시_가격음수인경우_예외처리(){
        Balance balance = Balance.create(100L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Product.create("A 상품", "좋은 상품", -100L, balance, ProductStatus.AVAILABLE);
        });

        assertEquals("상품 가격은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 상품_등록시_재고가_0이하인경우_예외처리(){
        Balance balance = Balance.create(-100L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Product.create("A 상품", "좋은 상품", 100L, balance, ProductStatus.AVAILABLE);
        });

        assertEquals("재고는 0 이상이어야 합니다.", e.getMessage());
    }

    @Test
    public void 상품_등록시_재고와_상태_일관성_예외처리(){
        Balance balance = Balance.create(0L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Product.create("A 상품", "좋은 상품", 100L, balance, ProductStatus.AVAILABLE);
        });

        assertEquals("재고가 없는 상품은 판매 상태일 수 없습니다.", e.getMessage());

    }

    @Test
    public void 재고_증가_성공(){
        Balance balance = Balance.create(100L);
        Product product = Product.create("A 상품", "좋은 상품", 2000L, balance, ProductStatus.AVAILABLE);

        product.increaseBalance(product.getProductId(), 100L);

        assertEquals(Long.valueOf(200L), product.getBalance().getQuantity());
    }

    @Test
    public void 재고_감소_성공(){
        Balance balance = Balance.create(200L);
        Product product = Product.create("A 상품", "좋은 상품", 2000L, balance, ProductStatus.AVAILABLE);

        product.decreaseBalance(product.getProductId(), 100L);

        assertEquals(Long.valueOf(100L), product.getBalance().getQuantity());

    }
}
