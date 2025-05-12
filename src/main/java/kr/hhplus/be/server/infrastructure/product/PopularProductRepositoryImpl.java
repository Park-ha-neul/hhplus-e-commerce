package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.PopularProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PopularProductRepositoryImpl implements PopularProductRepository {

    private final PopularProductJpaRepository popularProductJpaRepository;

    @Override
    public List<PopularProductProjection> findByPeriodAndDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return popularProductJpaRepository.findPopularProductsByPeriod(startDate, endDate, limit);
    }
}
