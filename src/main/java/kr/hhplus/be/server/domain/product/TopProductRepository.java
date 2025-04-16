package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TopProductRepository extends JpaRepository<TopProduct, Long> {
    Optional<TopProduct> findByProductId(Long productId);
    List<TopProduct> findByPeriodTypeAndCalculatedDateOrderByRankAsc(PeriodType periodType, LocalDate calculatedDate);
    Optional<TopProduct> findByProductIdAndPeriodTypeAndCalculatedDate(
            Long productId,
            PeriodType periodType,
            LocalDate calculatedDate
    );
}
