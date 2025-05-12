package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ProductServiceCacheTest {

    private static final Logger logger = LogManager.getLogger(ProductServiceCacheTest.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        productRepository.save(new Product(1L, "상품 A", "설명입니다.", 1000L, 100L, Product.ProductStatus.AVAILABLE));
    }

    @Test
    void 캐시에_저장되고_TTL이_적용되는지_확인() throws InterruptedException {
        String cacheKey = "product:detail:1";

        // 캐시에 값이 없음
        assertNull(redisTemplate.opsForValue().get(cacheKey));

        // 첫 조회 → DB에서 가져오고 캐시 저장
        Product product = productService.getProduct(1L);
        assertNotNull(product);

        // 캐시에 저장되었는지 확인
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        assertNotNull(cached);

        // TTL 확인 (Redis에서 TTL 조회)
        Long ttl = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
        assertNotNull(ttl);
        assertTrue(ttl <= 600 && ttl > 0); // 10분 이하, 0초 초과
    }
}
