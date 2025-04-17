package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TopProductEntityRepository extends JpaRepository<TopProductEntity, Long> {
    Optional<TopProductEntity> findByProductId(Long productId);
    List<TopProductEntity> findByPeriodTypeAndCalculatedDateOrderByRankAsc(
            PeriodType periodType,
            LocalDate calculatedDate,
            PageRequest pageRequest
    );
}
