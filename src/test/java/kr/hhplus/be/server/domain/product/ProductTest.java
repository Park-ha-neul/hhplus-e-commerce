package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    @Test
    void 상품_생성_성공() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .quantity(100L)
                .build();

        // when
        Product product = Product.create(request);

        // then
        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals(Long.valueOf(1000L), product.getPrice());
        assertEquals(Long.valueOf(100L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus());
    }

    @Test
    void 상품_가격이_음수일_때_예외_발생() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(-100L) // 잘못된 가격
                .quantity(100L)
                .build();

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Product.create(request);
        });
        assertEquals(ProductErrorCode.PRODUCT_PRICE_MUST_BE_POSITIVE.getMessage(), exception.getMessage());
    }

    @Test
    void 상품_재고가_음수일_때_예외_발생() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .quantity(-10L)
                .build();

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Product.create(request);
        });
        assertEquals(ProductErrorCode.PRODUCT_STOCK_MUST_BE_POSITIVE.getMessage(), exception.getMessage());
    }

    @Test
    void 재고_증가_성공() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .quantity(100L)
                .build();

        Product product = Product.create(request);

        // when
        product.increaseBalance(50L);

        // then
        assertEquals(Long.valueOf(150L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus()); // 재고가 0이 아니라면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_성공() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .quantity(100L)
                .build();

        Product product = Product.create(request);

        // when
        product.decreaseBalance(50L);

        // then
        assertEquals(Long.valueOf(50L), product.getQuantity());
        assertEquals(Product.ProductStatus.AVAILABLE, product.getStatus()); // 재고가 남아있다면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_후_품절_상태로_변경() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .quantity(10L)
                .build();

        Product product = Product.create(request);

        // when
        product.decreaseBalance(10L); // 재고를 모두 소진

        // then
        assertEquals(Long.valueOf(0L), product.getQuantity());
        assertEquals(Product.ProductStatus.SOLD_OUT, product.getStatus()); // 품절 상태로 변경되어야 함
    }
}
