package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopProductServiceTest {

    @InjectMocks
    private TopProductService topProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TopProductRepository topProductRepository;

    @Test
    void 일간_상위_상품_조회(){
        Long productId = 1L;
        PeriodType type = PeriodType.DAILY;
        Product product = Product.create("A 상품", "설명", 2000L, Balance.create(200L), ProductStatus.AVAILABLE);
        LocalDate calculatedDate = TopProduct.calculateDate(type);
        List<TopProduct> mockTopProduct = new ArrayList<>();
        TopProduct topProduct1 = TopProduct.create(product, type, calculatedDate,200L);
        TopProduct topProduct2 = TopProduct.create(product, type, calculatedDate,200L);
        mockTopProduct.add(topProduct1);
        mockTopProduct.add(topProduct2);

        when(topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate)).thenReturn(mockTopProduct);

        topProductService.getTopProductsByPeriod(type);

        verify(topProductRepository).findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate);
    }

    @Test
    void 주간_상위_상품_조회(){
        Long productId = 1L;
        PeriodType type = PeriodType.WEEKLY;
        Product product = Product.create("A 상품", "설명", 2000L, Balance.create(200L), ProductStatus.AVAILABLE);
        LocalDate calculatedDate = TopProduct.calculateDate(type);
        List<TopProduct> mockTopProduct = new ArrayList<>();
        TopProduct topProduct1 = TopProduct.create(product, type, calculatedDate,200L);
        TopProduct topProduct2 = TopProduct.create(product, type, calculatedDate,200L);
        mockTopProduct.add(topProduct1);
        mockTopProduct.add(topProduct2);

        when(topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate)).thenReturn(mockTopProduct);

        topProductService.getTopProductsByPeriod(type);

        verify(topProductRepository).findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate);
    }

    @Test
    void 월간_상위_상품_조회(){
        Long productId = 1L;
        PeriodType type = PeriodType.WEEKLY;
        Product product = Product.create("A 상품", "설명", 2000L, Balance.create(200L), ProductStatus.AVAILABLE);
        LocalDate calculatedDate = TopProduct.calculateDate(type);
        List<TopProduct> mockTopProduct = new ArrayList<>();
        TopProduct topProduct1 = TopProduct.create(product, type, calculatedDate,200L);
        TopProduct topProduct2 = TopProduct.create(product, type, calculatedDate,200L);
        mockTopProduct.add(topProduct1);
        mockTopProduct.add(topProduct2);

        when(topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate)).thenReturn(mockTopProduct);

        topProductService.getTopProductsByPeriod(type);

        verify(topProductRepository).findByPeriodTypeAndCalculatedDateOrderByRankAsc(type, calculatedDate);
    }

    @Test
    void 상위_상품_등록(){
        Product product = Product.create("상품 A", "설명", 2000L, Balance.create(100L), ProductStatus.AVAILABLE);
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(topProductRepository.save(any(TopProduct.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));  // 저장된 객체 그대로 반환

        TopProduct result = topProductService.registerTopProduct(product.getProductId(), PeriodType.DAILY, 2L);

        assertEquals(product, result.getProduct());
        verify(topProductRepository).save(any(TopProduct.class));
    }

    @Test
    void 상위_상품_등록시_상품이_없는경우_예외처리(){
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            topProductService.registerTopProduct(productId, PeriodType.DAILY, 10L);
        });

        assertEquals("상품을 찾을 수 없습니다.", e.getMessage());
    }
}
