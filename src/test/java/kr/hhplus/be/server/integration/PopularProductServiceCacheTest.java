package kr.hhplus.be.server.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductRepository;
import kr.hhplus.be.server.domain.product.PopularProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PopularProductServiceCacheTest {

    private static final Logger logger = LogManager.getLogger(PopularProductServiceCacheTest.class);


    @Autowired
    private PopularProductService popularProductService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PopularProductRepository popularProductRepository;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 캐시_동작_정상_작동_확인() {
        // DAILY PeriodType을 위한 캐시 동작 확인
        PopularProduct.PeriodType periodType = PopularProduct.PeriodType.DAILY;
        boolean asc = true;
        int limit = 5;

        // 첫 번째 호출: DB에서 가져오고 캐시에 저장
        List<PopularProduct> p1 = popularProductService.getPopularProducts(periodType, asc, limit);
        String cacheKey = "top:products:" + periodType.name() + ":" + LocalDate.now();
        logger.info("cacheKey : " + cacheKey);

        Object rawCached = redisTemplate.opsForValue().get(cacheKey);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<PopularProduct> cachedProducts = objectMapper.convertValue(
                rawCached,
                new TypeReference<List<PopularProduct>>() {}
        );

        assertNotNull(cachedProducts);
        assertEquals(p1.size(), cachedProducts.size());

        // 두 번째 호출: 캐시에서 가져오므로 DB를 조회하지 않음
        List<PopularProduct> p2 = popularProductService.getPopularProducts(periodType, asc, limit);
        for (int i = 0; i < p1.size(); i++) {
            PopularProduct product1 = p1.get(i);
            PopularProduct product2 = p2.get(i);

            assertEquals(product1.getProductId(), product2.getProductId());
            assertEquals(product1.getRanking(), product2.getRanking());
            assertEquals(product1.getTotalCount(), product2.getTotalCount());
            assertEquals(product1.getProductName(), product2.getProductName());
            assertEquals(product1.getPeriodType(), product2.getPeriodType());
        }
    }

    @Test
    void 캐시_만료_테스트() throws InterruptedException {
        PopularProduct.PeriodType periodType = PopularProduct.PeriodType.DAILY;
        String cacheKey = "top:products:" + periodType.name() + ":" + LocalDate.now();

        // 테스트용 TopProduct 리스트 생성
        List<PopularProduct> mockData = List.of(
                new PopularProduct(1L, "상품 A", 1000L, 100L, 2, periodType),
                new PopularProduct(2L, "상품 B", 1000L, 200L, 1, periodType)
        );

        redisTemplate.opsForValue().set(cacheKey, mockData, Duration.ofSeconds(1));
        logger.info("cacheKey 저장 완료: {}", cacheKey);

        assertNotNull(redisTemplate.opsForValue().get(cacheKey));

        Thread.sleep(1500);

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        List<PopularProduct> result = objectMapper.convertValue(cached, new TypeReference<List<PopularProduct>>() {});
        assertNull(result);
    }

    @Test
    void 기간유형에_따른_캐시_동작_테스트() {
        for (PopularProduct.PeriodType periodType : PopularProduct.PeriodType.values()) {
            List<PopularProduct> result = popularProductService.getPopularProducts(periodType, true, 5);

            logger.info("periodType: " + periodType.name() + " - 결과: " + result);
            assertFalse(result.isEmpty());

            String cacheKey = "top:products:" + periodType.name() + ":" + LocalDate.now();
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            logger.info("캐시에서 조회한 데이터: " + cached);
            assertNotNull(cached);
        }
    }
}
