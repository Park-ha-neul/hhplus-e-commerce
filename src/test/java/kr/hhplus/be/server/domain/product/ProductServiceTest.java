package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService productService;

    @Test
    void 상품_등록_성공(){
        Balance balance = Balance.create(200L);
        Product mockProduct = Product.create("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);

        when(repository.save(any(Product.class))).thenReturn(mockProduct);

        Product result = productService.createProduct("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);

        assertEquals(Long.valueOf(200L), result.getBalance().getQuantity());
        verify(repository).save(any(Product.class));
    }

    @Test
    void 상품_상세_조회(){
        Balance balance = Balance.create(20L);
        Product mockProduct = Product.create("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);

        when(repository.findById(mockProduct.getProductId())).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProductDetails(mockProduct.getProductId());

        assertEquals("A 상품", result.getName());
    }

    @Test
    void 상품_상세_조회시_상품이_없는경우_예외처리(){
        Long productId = 2L;

        when(repository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductDetails(productId);
        });

        assertEquals("상품이 존재하지 않습니다.", e.getMessage());
        verify(repository, times(1)).findById(productId);
    }

    @Test
    void 재고_확인(){
        Balance balance = Balance.create(200L);
        Product mockProduct = Product.create("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);
        Long productId = mockProduct.getProductId();
        when(repository.findById(productId)).thenReturn(Optional.of(mockProduct));

        Balance result = productService.getQuntity(productId);

        assertEquals(Long.valueOf(200L), result.getQuantity());
    }

    @Test
    void 상품_증가_성공(){
        Balance balance = Balance.create(200L);
        Product mockProduct = Product.create("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);
        Long productId = mockProduct.getProductId();

        when(repository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productService.increaseProductBalance(productId, 200L);

        assertEquals(Long.valueOf(400L), mockProduct.getBalance().getQuantity());
    }

    @Test
    void 상품_감소_성공(){
        Balance balance = Balance.create(400L);
        Product mockProduct = Product.create("A 상품", "설명", 2000L, balance, ProductStatus.AVAILABLE);
        Long productId = mockProduct.getProductId();

        when(repository.findById(productId)).thenReturn(Optional.of(mockProduct));

        productService.decreaseProductBalance(productId, 200L);

        assertEquals(Long.valueOf(200L), mockProduct.getBalance().getQuantity());
    }
}
