package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import kr.hhplus.be.server.support.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void 상품_생성_성공() {
        // given
        Long userId = 1L;
        Long productId = 2L;
        ProductRequest request = new ProductRequest("Product Name", "Description", 100L, 10L);

        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(user);
        Product savedProduct = new Product(productId, "상품 1", "상품 설명", 2000L, 30L, Product.ProductStatus.AVAILABLE);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(request, userId);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void 상품_생성_실패_관리자만() {
        Long userId = 1L;
        ProductRequest request = new ProductRequest("Product", "desc", 100L, 5L);

        User nonAdminUser = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(nonAdminUser);
        when(nonAdminUser.isAdmin()).thenReturn(false);

        // when & then
        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> productService.createProduct(request, userId)
        );

        assertEquals(ProductErrorCode.CREATE_PRODUCT_MUST_BE_ADMIN.getMessage(), exception.getMessage());
    }

    @Test
    void 상품_상세_조회_성공() {
        Long productId = 1L;
        Product product = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Product result = productService.getProduct(productId);

        // then
        assertEquals(product, result);
    }

    @Test
    void 상품_상세_조회_실패() {
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProduct(productId)
        );

        assertEquals(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 상품_재고_증가() {
        Long productId = 1L;
        Long quantity = 5L;

        Product product = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.increaseProductBalance(productId, quantity);

        // then
        verify(product).increaseBalance(quantity);
        verify(productRepository).save(product);
    }

    @Test
    void 상품_재고_감소() {
        Long productId = 1L;
        Long quantity = 2L;

        Product product = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.decreaseProductStock(productId, quantity);

        // then
        verify(product).decreaseBalance(quantity);
        verify(productRepository).save(product);
    }
}
