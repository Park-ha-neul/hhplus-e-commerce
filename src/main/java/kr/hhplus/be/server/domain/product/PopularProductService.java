package kr.hhplus.be.server.domain.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.infrastructure.product.PopularProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularProductService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PopularProductRepository popularProductRepository;

    private Duration getTTLByPeriodType(PopularProduct.PeriodType periodType) {
        return switch (periodType) {
            case DAILY -> Duration.ofDays(1);
            case WEEKLY -> Duration.ofDays(7);
            case MONTHLY -> Duration.ofDays(30);
        };
    }

    public List<PopularProduct> getPopularProducts(PopularProduct.PeriodType periodType, boolean ascending, int limit) {
        LocalDate now = LocalDate.now();
        LocalDateTime startDate = periodType.getStartDate(now).atStartOfDay();
        LocalDateTime endDate = now.atTime(LocalTime.MAX);
        String cacheKey = "top:products:" + periodType.name() + ":" + now;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(
                    cached,
                    new TypeReference<List<PopularProduct>>() {}
            );
        }

        List<PopularProductProjection> projections = popularProductRepository
                .findByPeriodAndDateRange(startDate, endDate, limit);

        List<PopularProduct> popularProducts = projections.stream()
                .map(p -> new PopularProduct(
                        p.getProductId(),
                        p.getProductName(),
                        p.getPrice(),
                        p.getTotalCount(),
                        p.getRanking(),
                        periodType
                ))
                .collect(Collectors.toList());

        Duration ttl = getTTLByPeriodType(periodType);
        redisTemplate.opsForValue().set(cacheKey, popularProducts, ttl);

        if(ascending){
            return popularProducts.stream()
                    .sorted(PopularProduct.rankAscComparator())
                    .collect(Collectors.toList());
        }else{
            return popularProducts.stream()
                    .sorted(PopularProduct.rankDescComparator())
                    .collect(Collectors.toList());
        }
    }
}
