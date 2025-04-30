package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    @Test
    void 상품_생성_성공() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                3000L,
                2000L
        );

        // when
        Product product = Product.create(command);

        // then
        assertNotNull(product);
        assertEquals("상품 A", product.getName());
        assertEquals(Long.valueOf(3000L), product.getPrice());
        assertEquals(Long.valueOf(2000L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus());
    }

    @Test
    void 상품_가격이_음수일_때_예외_발생() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                -3000L,
                2000L
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Product.create(command);
        });
        assertEquals(ProductErrorCode.PRODUCT_PRICE_MUST_BE_POSITIVE.getMessage(), exception.getMessage());
    }

    @Test
    void 상품_재고가_음수일_때_예외_발생() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                3000L,
                -2000L
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Product.create(command);
        });
        assertEquals(ProductErrorCode.PRODUCT_STOCK_MUST_BE_POSITIVE.getMessage(), exception.getMessage());
    }

    @Test
    void 재고_증가_성공() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                3000L,
                100L
        );

        Product product = Product.create(command);

        // when
        product.increaseBalance(50L);

        // then
        assertEquals(Long.valueOf(150L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus()); // 재고가 0이 아니라면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_성공() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                3000L,
                100L
        );

        Product product = Product.create(command);

        // when
        product.decreaseBalance(50L);

        // then
        assertEquals(Long.valueOf(50L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus()); // 재고가 남아있다면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_후_품절_상태로_변경() {
        // given
        ProductCommand command = new ProductCommand(
                "상품 A",
                "설명",
                3000L,
                10L
        );

        Product product = Product.create(command);

        // when
        product.decreaseBalance(10L); // 재고를 모두 소진

        // then
        assertEquals(Long.valueOf(0L), product.getQuantity());
        assertEquals(Product.ProductStatus.SOLD_OUT, product.getStatus()); // 품절 상태로 변경되어야 함
    }
}
