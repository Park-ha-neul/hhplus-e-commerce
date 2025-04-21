package kr.hhplus.be.server.domain.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TopProductRepository{
    TopProduct save (TopProduct topProduct);
    Optional<TopProduct> findByProductId(Long productId);
    List<TopProduct> findByPeriodTypeAndCalculatedDateOrderByRankAsc(
            TopProduct.PeriodType periodType,
            LocalDate calculatedDate,
            PageRequest pageRequest
    );
}
