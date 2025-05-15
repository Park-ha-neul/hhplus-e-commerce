package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssuedUser;
import kr.hhplus.be.server.domain.coupon.CouponIssuedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponIssuedUserRepositoryImpl implements CouponIssuedUserRepository {

    private final CouponIssuedUserJpaRepository couponIssuedUserJpaRepository;

    @Override
    public CouponIssuedUser save(CouponIssuedUser couponIssuedUser) {
        return couponIssuedUserJpaRepository.save(couponIssuedUser);
    }
}
