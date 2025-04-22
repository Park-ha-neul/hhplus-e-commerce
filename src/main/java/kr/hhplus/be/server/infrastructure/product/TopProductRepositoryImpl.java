package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
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
    public List<TopProduct> findByPeriodTypeAndCalculateDateOrderByRankAsc(TopProduct.PeriodType periodType, LocalDate calculatedDate, PageRequest pageRequest) {
        return topProductJpaRepository.findByPeriodTypeAndCalculateDateOrderByRankAsc(periodType, calculatedDate, pageRequest);
    }
}
