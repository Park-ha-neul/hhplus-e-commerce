package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class ProductEntityTest {

    @Test
    void 상품_생성_성공() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .balance(new Balance(100L)) // 초기 재고 100
                .build();

        // when
        ProductEntity productEntity = ProductEntity.create(request);

        // then
        assertNotNull(productEntity);
        assertEquals("Test Product", productEntity.getName());
        assertEquals(Long.valueOf(1000L), productEntity.getPrice());
        assertEquals(Long.valueOf(100L), productEntity.getBalance().getQuantity());
        assertEquals(ProductStatus.AVAILABLE, productEntity.getStatus());
    }

    @Test
    void 상품_가격이_음수일_때_예외_발생() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(-100L) // 잘못된 가격
                .balance(new Balance(100L))
                .build();

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ProductEntity.create(request);
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
                .balance(new Balance(-10L)) // 잘못된 재고
                .build();

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ProductEntity.create(request);
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
                .balance(new Balance(100L))
                .build();

        ProductEntity productEntity = ProductEntity.create(request);

        // when
        productEntity.increaseBalance(productEntity.getProductId(), 50L);

        // then
        assertEquals(Long.valueOf(150L), productEntity.getBalance().getQuantity());
        assertEquals(ProductStatus.AVAILABLE, productEntity.getStatus()); // 재고가 0이 아니라면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_성공() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .balance(new Balance(100L))
                .build();

        ProductEntity productEntity = ProductEntity.create(request);

        // when
        productEntity.decreaseBalance(productEntity.getProductId(), 50L);

        // then
        assertEquals(Long.valueOf(50L), productEntity.getBalance().getQuantity());
        assertEquals(ProductStatus.AVAILABLE, productEntity.getStatus()); // 재고가 남아있다면 상태는 AVAILABLE이어야 함
    }

    @Test
    void 재고_감소_후_품절_상태로_변경() {
        // given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(1000L)
                .balance(new Balance(10L)) // 재고가 10
                .build();

        ProductEntity productEntity = ProductEntity.create(request);

        // when
        productEntity.decreaseBalance(productEntity.getProductId(), 10L); // 재고를 모두 소진

        // then
        assertEquals(Long.valueOf(0L), productEntity.getBalance().getQuantity());
        assertEquals(ProductStatus.SOLD_OUT, productEntity.getStatus()); // 품절 상태로 변경되어야 함
    }
}
