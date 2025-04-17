package kr.hhplus.be.server.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponEntityRepository extends JpaRepository<UserCouponEntity, Long> {
    List<UserCouponEntity> findByUserId(Long userId);
    UserCouponEntity findByUserAndCoupon(Long userId, Long couponId);
}
