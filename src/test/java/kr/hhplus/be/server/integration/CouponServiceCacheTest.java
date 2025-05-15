package kr.hhplus.be.server.integration;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CouponServiceCacheTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LogManager.getLogger(CouponServiceCacheTest.class);

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
}
