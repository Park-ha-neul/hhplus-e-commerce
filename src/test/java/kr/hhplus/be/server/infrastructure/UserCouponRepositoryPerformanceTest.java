package kr.hhplus.be.server.infrastructure;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class UserCouponRepositoryPerformanceTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    private Long testUserId;

    @BeforeEach
    public void setUp() {
        testUserId = 1L;
    }

    @Test
    public void testFindByUserIdAndStatus_Performance() {
        long startTime = System.currentTimeMillis();

        // 성능 테스트: 특정 사용자와 상태로 쿠폰 조회
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(
                testUserId, UserCoupon.UserCouponStatus.ISSUED);

        long endTime = System.currentTimeMillis();

        System.out.println("조회 시간: " + (endTime - startTime) + "ms");
        System.out.println("조회된 쿠폰 수: " + userCoupons.size());

        // 결과 데이터 출력
        userCoupons.forEach(coupon -> {
            System.out.println("CouponId: " + coupon.getUserCouponId() +
                    ", Status: " + coupon.getStatus() +
                    ", UserId: " + coupon.getUserId());
        });

        assertNotNull(userCoupons);
    }
}
