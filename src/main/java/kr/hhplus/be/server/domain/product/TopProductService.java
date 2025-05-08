package kr.hhplus.be.server.domain.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopProductService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final TopProductRepository topProductRepository;
    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;

    private Duration getTTLByPeriodType(TopProduct.PeriodType periodType) {
        return switch (periodType) {
            case DAILY -> Duration.ofDays(1);
            case WEEKLY -> Duration.ofDays(7);
            case MONTHLY -> Duration.ofDays(30);
        };
    }

    public List<TopProduct> getTopProductsByPeriod(TopProduct.PeriodType periodType) {
        LocalDate calculateDate = TopProduct.calculateDate(periodType);
        String cacheKey = "top:products:" + periodType.name() + ":" + calculateDate;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(
                    cached,
                    new TypeReference<List<TopProduct>>() {}
            );
        }

        PageRequest topN = PageRequest.of(0, 5);
        List<TopProduct> topProducts = topProductRepository.findByPeriodTypeAndCalculateDateOrderByRankAsc(periodType, calculateDate, topN);

        Duration ttl = getTTLByPeriodType(periodType);
        redisTemplate.opsForValue().set(cacheKey, topProducts, ttl);

        return topProducts;
    }

    @Cacheable(value = "topProducts", key = "#topProductId", condition = "#topProductId != null", cacheManager = "redisCacheManager")
    public TopProduct getTopProductById(Long topProductId) {
        TopProduct topProduct = topProductRepository.findById(topProductId);
        Product product = productRepository.findById(topProduct.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        topProduct.setProduct(product);
        return topProduct;
    }
}
