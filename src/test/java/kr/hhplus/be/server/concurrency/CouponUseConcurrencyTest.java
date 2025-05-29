package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.coupon.*;
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
public class CouponUseConcurrencyTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private Long userCouponId;

    @BeforeEach
    void setUp(){
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();
        Coupon coupon = new Coupon("쿠폰 A", 10L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        Long couponId = coupon.getCouponId();
        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCoupon.UserCouponStatus.ISSUED);
        userCouponRepository.save(userCoupon);
        userCouponId = userCoupon.getUserCouponId();
    }

    @Test
    void 쿠폰_사용_분산락_동시성_테스트() throws InterruptedException{
        int threadCount = 4; // 발급 시도 스레드 수
        CountDownLatch latch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    userCouponService.useWithLock(userCouponId);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserCoupon updatedCoupon = userCouponRepository.findById(userCouponId);
        assertEquals(updatedCoupon.getStatus(), UserCoupon.UserCouponStatus.USED);
        assertEquals(3, failCount.get());
    }
}
