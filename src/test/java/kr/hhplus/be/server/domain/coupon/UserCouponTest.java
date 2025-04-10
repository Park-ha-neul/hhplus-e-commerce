package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponTest {

    @Test
    void 쿠폰_정상_사용(){
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setStatus(CouponStatus.ISSUED);

        userCoupon.use();

        assertEquals(CouponStatus.USED, userCoupon.getStatus());
    }

    @Test
    void 이미_사용된_쿠폰_예외발생(){
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setStatus(CouponStatus.USED);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCoupon.use();
        });

        assertEquals("이미 사용된 쿠폰입니다.", e.getMessage());
    }
}
