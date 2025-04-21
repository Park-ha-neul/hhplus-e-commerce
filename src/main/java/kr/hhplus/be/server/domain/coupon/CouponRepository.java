package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(Long couponId);
    List<Coupon> findAllByStatus(Coupon.CouponStatus status);
    List<Coupon> findAllCoupons();
}
