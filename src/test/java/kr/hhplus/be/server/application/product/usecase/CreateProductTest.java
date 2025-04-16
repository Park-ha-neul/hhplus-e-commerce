package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProductTest {

    @InjectMocks
    private CreateProduct createProduct;

    @Mock
    private ProductService productService;

    @Test
    void 관리자가_아닌데_상품을_등록한경우_예외처리(){
        User user = User.create(1L, false);

        AccessDeniedException e = assertThrows(AccessDeniedException.class, () -> {
            createProduct.create(user, "상품 A", "설명", 2000L, 100L, ProductStatus.AVAILABLE);
        });

        assertEquals("관리자만 상품을 등록할 수 있습니다.", e.getMessage());
    }

    @Test
    void 관리자인_경우_상품등록_성공() throws AccessDeniedException {
        User user = User.create(1L, true);

        createProduct.create(user, "상품 A", "설명", 2000L, 100L, ProductStatus.AVAILABLE);
        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);

        verify(productService).createProduct(
                eq("상품 A"),
                eq("설명"),
                eq(2000L),
                balanceCaptor.capture(),
                eq(ProductStatus.AVAILABLE)
        );

    }
}
