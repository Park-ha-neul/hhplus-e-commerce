package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularProductService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PopularProductRedisService popularProductRedisService;
    private final PopularProductMapper popularProductMapper;

    public List<PopularProduct> getPopularProducts(PopularProduct.PeriodType periodType, boolean ascending, int limit) {
        LocalDate now = LocalDate.now();
        String zsetKey = popularProductRedisService.getPeriodKey(periodType, now);

        Set<ZSetOperations.TypedTuple<Object>> tuples = popularProductRedisService.getZSetData(zsetKey);
        
        if (tuples == null || tuples.isEmpty()) {
            List<String> dailyKeys = popularProductRedisService.getDailyKeysForPeriod(periodType, now);

            if (!dailyKeys.isEmpty()) {
                String baseKey = dailyKeys.get(0);
                List<String> otherKeys = dailyKeys.subList(1, dailyKeys.size());

                redisTemplate.opsForZSet().unionAndStore(baseKey, otherKeys, zsetKey);

                tuples = popularProductRedisService.getZSetData(zsetKey);

                if (tuples != null && !tuples.isEmpty()) {
                    Duration ttl = popularProductRedisService.getTTLByPeriodType(periodType);
                    redisTemplate.expire(zsetKey, ttl);
                }
            }
        }

        List<PopularProduct> popularProducts = popularProductMapper.fromRedisTuples(tuples, ascending);
        return popularProducts.stream().limit(limit).collect(Collectors.toList());
    }

    public void incrementProductScore(Long productId, Long salesCount){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateKey = LocalDate.now().format(formatter);
        String redisKey = "popular:products:" + dateKey;

        redisTemplate.opsForZSet().incrementScore(redisKey, productId.toString(), salesCount.doubleValue());
        redisTemplate.expire(redisKey, 30, TimeUnit.DAYS);
    }
}
