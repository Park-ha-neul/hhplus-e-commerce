package kr.hhplus.be.server.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponEntityRepository extends JpaRepository<CouponEntity, Long> {
    List<CouponEntity> findAllByStatus(CouponStatus status);
}
