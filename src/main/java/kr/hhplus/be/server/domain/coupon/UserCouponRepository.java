package kr.hhplus.be.server.domain.coupon;

import java.util.List;

public interface UserCouponRepository{
    UserCoupon save(UserCoupon userCoupon);
    UserCoupon findById(Long userCouponId);
    List<UserCoupon> findByUserId(Long userId);
    List<UserCoupon> findByUserIdAndStatus(Long userId, UserCoupon.UserCouponStatus status);
}
