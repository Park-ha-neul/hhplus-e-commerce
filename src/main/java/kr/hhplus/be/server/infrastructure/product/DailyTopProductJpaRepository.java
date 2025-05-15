package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.DailyTopProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyTopProductJpaRepository extends JpaRepository<DailyTopProduct, Long> {
    void deleteByDate(LocalDate date);
}
