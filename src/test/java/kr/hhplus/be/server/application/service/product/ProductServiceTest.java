package kr.hhplus.be.server.application.service.product;

import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void 상품이_조회시_상품이_존재하는_경우(){
        Long productId = 1L;
        Product product = new Product(1L, "상품 A", "좋은 상품", 1000L, new Balance(10L), ProductStatus.AVAILABLE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProduct(productId);

        assertEquals("상품 A", result.getName());
    }

    @Test
    public void 상품_조회시_상품이_없는경우_예외처리(){
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProduct(productId);
        });

        assertEquals("상품이 존재하지 않습니다.", e.getMessage());
    }

    @Test
    public void 판매_가능한_상품_목록_조회(){
        Product product1 = new Product(1L, "상품 A", "좋은 상품", 1000L, new Balance(10L), ProductStatus.AVAILABLE);
        Product product2 = new Product(2L, "상품 B", "좋은 상품", 1000L, new Balance(10L), ProductStatus.AVAILABLE);

        when(productRepository.findByStatus(ProductStatus.AVAILABLE)).thenReturn(Arrays.asList(product1, product2));

        List<Product> availableProducts = productService.getAvailableProducts();

        assertNotNull(availableProducts);
        assertEquals(2, availableProducts.size());
        assertEquals("상품 A", availableProducts.get(0).getName());
        assertEquals("상품 B", availableProducts.get(1).getName());
    }

    @Test
    public void 판매_가능한_상품_목록이_없을때(){
        when(productRepository.findByStatus(ProductStatus.AVAILABLE)).thenReturn(Collections.emptyList());

        List<Product> availableProducts = productService.getAvailableProducts();

        assertNotNull(availableProducts);
        assertTrue(availableProducts.isEmpty());
    }

    @Test
    public void 상품_상태_품절로_업데이트(){
        Long productId = 1L;
        Product product = new Product(1L, "상품 A", "좋은 상품", 1000L, new Balance(0L), ProductStatus.AVAILABLE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.updateProductStatus(productId, ProductStatus.SOLD_OUT);

        assertEquals("상품 A", product.getName());
        assertEquals(ProductStatus.SOLD_OUT, product.getStatus());
    }

    @Test
    public void 상품_상태_판매중으로_업데이트(){
        Long productId = 1L;
        Product product = new Product(1L, "상품 A", "좋은 상품", 1000L, new Balance(10L), ProductStatus.SOLD_OUT);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.updateProductStatus(productId, ProductStatus.AVAILABLE);

        assertEquals("상품 A", product.getName());
        assertEquals(ProductStatus.AVAILABLE, product.getStatus());
    }

    @Test
    public void 상품_등록_성공(){
        Long productId = 1L;
        Product product = new Product(productId, "상품 A", "좋은 상품", 1000L, new Balance(10L), ProductStatus.AVAILABLE);
        when(productRepository.save(product)).thenReturn(product);

        productService.createProduct(product);

        assertEquals("상품 A", product.getName());
    }

    @Test
    public void 상품_삭제_성공(){
        Long productId = 1L;
        Product product = new Product(productId, "상품 A", "좋은 상품", 1000L, new Balance(10L), ProductStatus.AVAILABLE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository).delete(product);
    }
}
