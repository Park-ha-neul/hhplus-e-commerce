package kr.hhplus.be.server.application.product.usecase;

import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.product.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTopProductTest {

    @InjectMocks
    private CreateTopProduct createTopProduct;

    @Mock
    private TopProductRepository topProductRepository;

    @Mock
    private TopProductService topProductService;


    @Test
    void 상위상품을_처음등록하는경우(){
        Long productId = 1L;
        PeriodType type = PeriodType.DAILY;
        Long count = 10L;

        Product product = Product.create("A 상품", "설명", 2000L, Balance.create(200L), ProductStatus.AVAILABLE);

        TopProduct topProduct = TopProduct.create(product, type, TopProduct.calculateDate(type), count);
        when(topProductRepository.findByProductId(productId)).thenReturn(Optional.of(topProduct));
        createTopProduct.registerOrUpdateTopProduct(productId, type, count);

        verify(topProductRepository).save(any(TopProduct.class));
    }

    @Test
    void 상위상품으로_이미등록했는데_또_등록하는경우(){
        Long productId = 1L;
        PeriodType type = PeriodType.DAILY;
        Long count = 10L;

        Product product = Product.create("A 상품", "설명", 2000L, Balance.create(200L), ProductStatus.AVAILABLE);
        TopProduct topProduct = TopProduct.create(product, type, TopProduct.calculateDate(type), 5L);

        when(topProductRepository.findByProductId(productId)).thenReturn(Optional.of(topProduct));

        // when
        createTopProduct.registerOrUpdateTopProduct(productId, type, count);

        // then
        assertEquals(Long.valueOf(15L), topProduct.getTotalCount());
        verify(topProductRepository).save(topProduct);
        verify(topProductService, never()).registerTopProduct(any(), any(), any());
    }

}
