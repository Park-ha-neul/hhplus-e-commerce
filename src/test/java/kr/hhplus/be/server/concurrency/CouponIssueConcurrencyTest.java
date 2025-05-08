package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class CouponIssueConcurrencyTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    private Long couponId;
    private Long userId;

    @BeforeEach
    void setUp(){
        User user = new User("하늘", false);
        userRepository.save(user);
        userId = user.getUserId();
        Coupon coupon = new Coupon("쿠폰 A", 10L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        couponId = coupon.getCouponId();
    }

    @Test
    void 쿠폰_발급_분산락_동시성_테스트() throws InterruptedException {
        int threadCount = 15; // 발급 시도 스레드 수
        CountDownLatch latch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    userCouponService.issueWithLock(userId, couponId);  // 쿠폰 발급 시도
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Coupon updatedCoupon = couponRepository.findById(couponId).orElseThrow();
        assertEquals(Long.valueOf(10L), updatedCoupon.getIssuedCount());
        assertEquals(5, failCount.get());
    }
}
