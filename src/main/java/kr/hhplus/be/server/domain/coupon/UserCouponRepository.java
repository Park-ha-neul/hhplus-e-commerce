package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository{
    UserCoupon save(UserCoupon userCoupon);
    Optional<UserCoupon> findById(Long userCouponId);
    List<UserCoupon> findByUserId(Long userId);
    List<UserCoupon> findByUserIdAndStatus(Long userId, UserCoupon.UserCouponStatus status);
}
