package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserCouponTest {

    @Test
    void 쿠폰_정상_발급(){
        User user = User.create(1L, false);
        Coupon coupon = mock(Coupon.class);

        UserCoupon result = UserCoupon.issueTo(user, coupon);

        assertEquals(CouponStatus.ISSUED, result.getStatus());
    }

    @Test
    void 쿠폰_사용(){
        User user = User.create(1L, false);
        Coupon coupon = mock(Coupon.class);

        UserCoupon result = UserCoupon.issueTo(user, coupon);
        result.use();
        assertEquals(CouponStatus.USED, result.getStatus());
    }

    @Test
    void 이미_사용된_쿠폰_예외발생(){
        User user = User.create(1L, false);
        Coupon coupon = mock(Coupon.class);

        UserCoupon result = UserCoupon.issueTo(user, coupon);

        result.use();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            result.use();
        });

        assertEquals(ErrorCode.ALREADY_USED_COUPON.getMessage(), e.getMessage());
    }
}
