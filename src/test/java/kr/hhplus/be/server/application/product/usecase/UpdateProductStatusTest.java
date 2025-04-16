package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductStatusTest {

    @InjectMocks
    private UpdateProductStatus updateProductStatus;

    @Mock
    private ProductService productService;

    @Test
    void 상품_감소후_상태_업데이트(){
        Balance balance = Balance.create(200L);
        Product product = Product.create("상품 A", "설명", 2000L, balance, ProductStatus.AVAILABLE);

        when(productService.getProductDetails(product.getProductId())).thenReturn(product);
        updateProductStatus.updateProductStatus(product.getProductId(), -100L);

        verify(productService, times(1)).decreaseProductBalance(product.getProductId(), 100L);
        assertEquals(Long.valueOf(100L), product.getBalance().getQuantity());
    }

    @Test
    void 상품_증가후_상태_업데이트(){
        Balance balance = Balance.create(100L);
        Product product = Product.create("상품 A", "설명", 2000L, balance, ProductStatus.AVAILABLE);
        when(productService.getProductDetails(product.getProductId())).thenReturn(product);
        updateProductStatus.updateProductStatus(product.getProductId(), 100L);

        assertEquals(Long.valueOf(200L), product.getBalance().getQuantity());
        verify(productService, times(1)).increaseProductBalance(product.getProductId(), 100L);

    }
}
