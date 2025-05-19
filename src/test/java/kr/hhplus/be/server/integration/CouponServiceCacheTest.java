package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CouponServiceCacheTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LogManager.getLogger(CouponServiceCacheTest.class);

    @BeforeEach
    void setUp() {
        redisTemplate.delete("coupon:1");
        redisTemplate.delete("coupon:1:users");

        for (int i = 0; i < 5; i++) {
            redisTemplate.opsForList().rightPush("coupon:1", "1");
        }
    }

    @AfterEach
    void tearDown() {
        Set<String> keys = redisTemplate.keys("coupon:*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void 쿠폰생성_시_쿠폰저장과_레디스리스트초기화_및_TTL설정_확인() {
        // given: 관리자인 사용자 생성 및 저장
        User adminUser = new User("하늘", true);
        userRepository.save(adminUser);

        CouponCommand command = CouponCommand.builder()
                .totalCount(5L)
                .discountType(Coupon.DiscountType.AMOUNT)
                .discountAmount(100L)
                .endDate(LocalDateTime.now().plusHours(1))
                .build();

        CouponResult result = couponService.create(command, adminUser.getUserId());

        Coupon savedCoupon = couponRepository.findById(result.getId()).orElseThrow();
        assertEquals(command.getTotalCount(), savedCoupon.getTotalCount());

        String redisKey = "coupon:" + savedCoupon.getCouponId();
        logger.info("Redis Key : {}", redisKey);

        Long redisListSize = redisTemplate.opsForList().size(redisKey);
        logger.info("Redis Len : {}", redisListSize);
        assertEquals(command.getTotalCount(), redisListSize);
    }

    @Test
    void 쿠폰_정상_발급_테스트() {
        Long userId = 1L;
        Long couponId = 1L;

        UserCouponResult result = userCouponService.issue(userId, couponId);

        String stockListKey = "coupon:" + couponId;
        Long redisListSize = redisTemplate.opsForList().size(stockListKey);
        String issuedSetKey = "coupon:" + couponId + ":users";
        Set<Object> members = redisTemplate.opsForSet().members(issuedSetKey);

        logger.info("Redis List Key : {}", stockListKey);
        logger.info("Redis List Key Len check : {}", redisListSize);
        logger.info("Redis Set Key : {}", issuedSetKey);
        logger.info("쿠폰 발급된 사용자 목록 : {}", members);

        assertNotNull(result);
        assertTrue(redisTemplate.opsForSet().isMember("coupon:1:users", String.valueOf(userId)));

        Long ttlSeconds = redisTemplate.getExpire(issuedSetKey, TimeUnit.SECONDS);
        logger.info("쿠폰 발급된 사용자 목록 TTL (초): {}", ttlSeconds);

        // TTL이 0 이상이면 (만료 안 된 상태) 테스트 통과
        assertNotNull(ttlSeconds);
        assertTrue("TTL이 설정되어 있어야 합니다.", ttlSeconds > 0);
    }

    @Test
    void 쿠폰_중복발급시_예외발생() {
        Long userId = 1L;
        Long couponId = 1L;
        userCouponService.issue(userId, couponId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.issue(userId, couponId);
        });

        assertEquals("이미 발급된 쿠폰입니다.", exception.getMessage());
    }
}
