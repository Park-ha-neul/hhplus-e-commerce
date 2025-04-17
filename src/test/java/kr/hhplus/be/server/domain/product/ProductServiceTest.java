package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import kr.hhplus.be.server.support.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductEntityRepository productEntityRepository;

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productEntityRepository, userPointEntityRepository);
    }

    @Test
    void 상품_생성_성공() {
        // given
        Long userId = 1L;
        ProductRequest request = new ProductRequest("Product Name", "Description", 100L, new Balance(10L));

        // UserPointEntity를 mock
        UserPointEntity userPointEntity = mock(UserPointEntity.class);
        when(userPointEntity.getUser()).thenReturn(mock(User.class));
        when(userPointEntity.getUser().isAdmin()).thenReturn(true);
        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(userPointEntity));

        // ProductEntity의 정적 메서드 mock
        try (MockedStatic<ProductEntity> mockedStatic = mockStatic(ProductEntity.class)) {
            ProductEntity productEntity = mock(ProductEntity.class);
            mockedStatic.when(() -> ProductEntity.create(request)).thenReturn(productEntity);

            when(productEntityRepository.save(any(ProductEntity.class))).thenReturn(productEntity);

            // when
            ProductEntity result = productService.createProduct(request, userId);

            // then
            assertNotNull(result);
            verify(productEntityRepository).save(productEntity);
        }
    }

    @Test
    void 상품_생성_실패_관리자만() {
        // given
        Long userId = 1L;
        ProductRequest request = new ProductRequest("Product Name", "Description", 100L, new Balance(10L));
        UserPointEntity userPointEntity = mock(UserPointEntity.class);
        when(userPointEntity.getUser()).thenReturn(mock(User.class));
        when(userPointEntity.getUser().isAdmin()).thenReturn(false);

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(userPointEntity));

        // when & then
        assertThrows(ForbiddenException.class, () -> productService.createProduct(request, userId));
    }

    @Test
    void 상품_상세_조회_성공() {
        // given
        Long productId = 1L;
        ProductEntity productEntity = mock(ProductEntity.class);
        when(productEntityRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        // when
        ProductEntity result = productService.getProductDetails(productId);

        // then
        assertNotNull(result);
    }

    @Test
    void 상품_상세_조회_실패() {
        // given
        Long productId = 1L;
        when(productEntityRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> productService.getProductDetails(productId));
    }

    @Test
    void 상품_재고_증가() {
        // given
        Long productId = 1L;
        Long quantity = 5L;
        ProductEntity productEntity = mock(ProductEntity.class);
        when(productEntityRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        // when
        productService.increaseProductBalance(productId, quantity);

        // then
        verify(productEntity).increaseBalance(productId, quantity);
        verify(productEntityRepository).save(productEntity);
    }

    @Test
    void 상품_재고_감소() {
        // given
        Long productId = 1L;
        Long quantity = 3L;
        ProductEntity productEntity = mock(ProductEntity.class);
        when(productEntityRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        // when
        productService.decreaseProductBalance(productId, quantity);

        // then
        verify(productEntity).decreaseBalance(productId, quantity);
        verify(productEntityRepository).save(productEntity);
    }
}
