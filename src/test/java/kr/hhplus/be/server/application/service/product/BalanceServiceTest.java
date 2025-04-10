package kr.hhplus.be.server.application.service.product;

import kr.hhplus.be.server.domain.common.ProductStatus;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    public void 재고_조회_성공(){
        Long productId = 1L;
        Product product = new Product(productId, "상품 A", "좋은 상품", 1000L, new Balance(), ProductStatus.AVAILABLE);
        when(productService.getProduct(productId)).thenReturn(product);

        Long stock = balanceService.getStock(productId);

        assertEquals(Long.valueOf(10L), stock);
    }

    @Test
    public void 재고_감소_성공(){
        Long productId = 1L;
        Long amount = 10L;
        Product product = new Product(productId, "상품 A", "좋은 상품", 1000L, new Balance(), ProductStatus.AVAILABLE);
        when(productService.getProduct(productId)).thenReturn(product);

        balanceService.decreaseStock(productId, amount);

        assertEquals(Long.valueOf(0L), product.getBalance());
        verify(productService).updateProductStatus(productId, ProductStatus.SOLD_OUT);
        verify(productRepository).save(product);
    }

    @Test
    public void 재고_증가_성공(){
        Long productId = 1L;
        Long amoun = 10L;
        Product product = new Product(productId, "상품 A", "좋은 상품", 1000L, new Balance(), ProductStatus.AVAILABLE);
        when(productService.getProduct(productId)).thenReturn(product);

        balanceService.increaseStock(productId, amoun);

        assertEquals(Long.valueOf(20L), product.getBalance());
        verify(productService).updateProductStatus(productId, ProductStatus.AVAILABLE);
        verify(productRepository).save(product);
    }
}
