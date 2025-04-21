package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TopProductRepositoryImpl implements TopProductRepository {

    private final TopProductJpaRepository topProductJpaRepository;

    @Override
    public TopProduct save(TopProduct topProduct) {
        return topProductJpaRepository.save(topProduct);
    }

    @Override
    public Optional<TopProduct> findByProductId(Long productId) {
        return topProductJpaRepository.findByProductId(productId);
    }

    @Override
    public List<TopProduct> findByPeriodTypeAndCalculatedDateOrderByRankAsc(TopProduct.PeriodType periodType, LocalDate calculatedDate, PageRequest pageRequest) {
        return topProductJpaRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculatedDate, pageRequest);
    }
}
