package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    boolean existsByUserAndCoupon(UserPoint user, Coupon coupon);
}
