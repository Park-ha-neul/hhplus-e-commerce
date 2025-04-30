package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCouponResult {
    private Long id;
    private Long userId;
    private Long couponId;
    private UserCoupon.UserCouponStatus status;

    public static UserCouponResult of(UserCoupon userCoupon){
        return new UserCouponResult(
                userCoupon.getUserCouponId(),
                userCoupon.getUserId(),
                userCoupon.getCouponId(),
                userCoupon.getStatus()
        );
    }
}
