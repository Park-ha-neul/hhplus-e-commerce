package kr.hhplus.be.server.domain.product;

import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TopProductRepository{
    TopProduct save (TopProduct topProduct);
    Optional<TopProduct> findByProductId(Long productId);
    List<TopProduct> findByPeriodTypeAndCalculateDateOrderByRankAsc(
            TopProduct.PeriodType periodType,
            LocalDate calculateDate,
            PageRequest pageRequest
    );
}
