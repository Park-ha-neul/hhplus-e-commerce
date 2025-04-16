package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TopProductService {

    private TopProductRepository topProductRepository;
    private ProductRepository productRepository;

    public TopProduct registerTopProduct(Long productId, PeriodType periodType, Long count) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        LocalDate calculatedDate = TopProduct.calculateDate(periodType);

        TopProduct topProduct = TopProduct.create(product, periodType, calculatedDate, count);
        return topProductRepository.save(topProduct);
    }

    public List<TopProduct> getTopProductsByPeriod(PeriodType periodType) {
        LocalDate calculateDate = TopProduct.calculateDate(periodType);
        return topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculateDate);
    }
}
