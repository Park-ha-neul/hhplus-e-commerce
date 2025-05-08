package kr.hhplus.be.server.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductRepository;
import kr.hhplus.be.server.domain.product.TopProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TopProductServiceTest {

    private static final Logger logger = LogManager.getLogger(TopProductServiceTest.class);

    @Autowired
    private TopProductService topProductService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TopProductRepository topProductRepository;

    @BeforeEach
    void setUp() {
        TopProduct topProduct = new TopProduct(1L, 1, 20L, LocalDate.now(), TopProduct.PeriodType.DAILY);
        topProductRepository.save(topProduct);
    }

    @Test
    void 캐시_동작_정상_작동_확인() {
        // DAILY PeriodType을 위한 캐시 동작 확인
        TopProduct.PeriodType periodType = TopProduct.PeriodType.DAILY;

        // 첫 번째 호출: DB에서 가져오고 캐시에 저장
        List<TopProduct> topProducts1 = topProductService.getTopProductsByPeriod(periodType);
        String cacheKey = "top:products:" + periodType.name() + ":" + LocalDate.now();
        logger.info("cacheKey : " + cacheKey);

        Object rawCached = redisTemplate.opsForValue().get(cacheKey);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        List<TopProduct> cachedProducts = objectMapper.convertValue(
                rawCached,
                new TypeReference<List<TopProduct>>() {}
        );

        assertNotNull(cachedProducts);
        assertEquals(topProducts1.size(), cachedProducts.size());

        // 두 번째 호출: 캐시에서 가져오므로 DB를 조회하지 않음
        List<TopProduct> topProducts2 = topProductService.getTopProductsByPeriod(periodType);
        for (int i = 0; i < topProducts1.size(); i++) {
            TopProduct product1 = topProducts1.get(i);
            TopProduct product2 = topProducts2.get(i);

            assertEquals(product1.getTopProductId(), product2.getTopProductId());
            assertEquals(product1.getProductId(), product2.getProductId());
            assertEquals(product1.getRank(), product2.getRank());
            assertEquals(product1.getTotalCount(), product2.getTotalCount());
            assertEquals(product1.getCalculateDate(), product2.getCalculateDate());
            assertEquals(product1.getPeriodType(), product2.getPeriodType());
        }
    }

    @Test
    void 캐시_만료_테스트() throws InterruptedException {
        TopProduct.PeriodType periodType = TopProduct.PeriodType.DAILY;
        LocalDate calculateDate = TopProduct.calculateDate(periodType);
        String cacheKey = "top:products:" + periodType.name() + ":" + calculateDate;

        // 테스트용 TopProduct 리스트 생성
        List<TopProduct> mockData = List.of(
                new TopProduct(1L, 1, 100L, calculateDate, periodType),
                new TopProduct(2L, 2, 50L, calculateDate, periodType)
        );

        redisTemplate.opsForValue().set(cacheKey, mockData, Duration.ofSeconds(1));
        logger.info("cacheKey 저장 완료: {}", cacheKey);

        assertNotNull(redisTemplate.opsForValue().get(cacheKey));

        Thread.sleep(1500);

        Object expired = redisTemplate.opsForValue().get(cacheKey);
        assertNull(expired);
    }

    @Test
    void 기간유형에_따른_캐시_동작_테스트() {
        // 여러 PeriodType에 대한 캐시 동작 확인
        LocalDate dailyCalculateDate = TopProduct.calculateDate(TopProduct.PeriodType.DAILY);
        LocalDate weeklyCalculateDate = TopProduct.calculateDate(TopProduct.PeriodType.WEEKLY);
        LocalDate monthlyCalculateDate = TopProduct.calculateDate(TopProduct.PeriodType.MONTHLY);

        List<TopProduct> dailyProducts = topProductService.getTopProductsByPeriod(TopProduct.PeriodType.DAILY);
        List<TopProduct> weeklyProducts = topProductService.getTopProductsByPeriod(TopProduct.PeriodType.WEEKLY);
        List<TopProduct> monthlyProducts = topProductService.getTopProductsByPeriod(TopProduct.PeriodType.MONTHLY);

        assertNotEquals(dailyProducts, weeklyProducts);
        assertNotEquals(dailyProducts, monthlyProducts);
        assertNotEquals(weeklyProducts, monthlyProducts);

        String dailyCacheKey = "top:products:" + TopProduct.PeriodType.DAILY.name() + ":" + dailyCalculateDate;
        String weeklyCacheKey = "top:products:" + TopProduct.PeriodType.WEEKLY.name() + ":" + weeklyCalculateDate;
        String monthlyCacheKey = "top:products:" + TopProduct.PeriodType.MONTHLY.name() + ":" + monthlyCalculateDate;
        logger.info("dailyCacheKey : " + dailyCacheKey);
        logger.info("weeklyCacheKey : " + weeklyCacheKey);
        logger.info("monthlyCacheKey : " + monthlyCacheKey);

        assertNotNull(redisTemplate.opsForValue().get(dailyCacheKey));
        assertNotNull(redisTemplate.opsForValue().get(weeklyCacheKey));
        assertNotNull(redisTemplate.opsForValue().get(monthlyCacheKey));
    }
}