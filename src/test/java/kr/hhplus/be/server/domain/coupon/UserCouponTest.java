package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponTest {
    @Test
    void 쿠폰_정상_발급(){
        Long userId = 1L;
        Long couponId = 2L;

        UserCoupon result = new UserCoupon(userId, couponId);

        assertEquals(UserCoupon.UserCouponStatus.ISSUED, result.getStatus());
    }

    @Test
    void 쿠폰_사용(){
        Long userId = 1L;
        Long couponId = 2L;

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.use();

        assertEquals(UserCoupon.UserCouponStatus.USED, userCoupon.getStatus());
    }

    @Test
    void 이미_사용된_쿠폰_예외발생(){
        Long userId = 1L;
        Long couponId = 2L;

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCoupon.use();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCoupon.use();
        });

        assertEquals(ErrorCode.ALREADY_USED_COUPON.getMessage(), e.getMessage());
    }
}
