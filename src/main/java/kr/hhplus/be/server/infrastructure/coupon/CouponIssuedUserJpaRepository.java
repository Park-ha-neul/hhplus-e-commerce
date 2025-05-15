package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssuedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuedUserJpaRepository extends JpaRepository<CouponIssuedUser, Long> {
}
