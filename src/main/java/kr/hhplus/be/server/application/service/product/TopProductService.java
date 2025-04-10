package kr.hhplus.be.server.application.service.product;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TopProductService {

    @Autowired
    private TopProductRepository topProductRepository;

    @Autowired
    private ProductRepository productRepository;

    // 주기 별 상위 상품 조회
    public List<TopProduct> getTopProducts(PeriodType periodType){
        LocalDate calculatedDate = TopProduct.calculateCalculatedDate(periodType);

        List<TopProduct> topProducts = topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculatedDate);

        return topProducts;
    }

    @Transactional
    public void saveOrUpdateTopProduct(Long productId, Long count, LocalDate date, PeriodType periodType) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Optional<TopProduct> existing = topProductRepository.findByProductIdAndPeriodTypeAndCalculatedDate(
                product, periodType, date
        );

        if (existing.isPresent()) {
            TopProduct top = existing.get();
            top.addCount(count);
        } else {
            TopProduct newTopProduct = TopProduct.create(product, periodType, date, count);
            topProductRepository.save(newTopProduct);
        }
    }
}
