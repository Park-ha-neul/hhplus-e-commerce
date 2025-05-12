package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.product.PopularProductProjection;
import java.time.LocalDateTime;
import java.util.List;

public interface PopularProductRepository {
    List<PopularProductProjection> findByPeriodAndDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit);
}
