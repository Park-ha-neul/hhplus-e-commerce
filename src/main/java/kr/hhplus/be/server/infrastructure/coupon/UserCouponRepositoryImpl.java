package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public Optional<UserCoupon> findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId);
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCouponJpaRepository.findByUserId(userId);
    }

    @Override
    public List<UserCoupon> findByUserIdAndStatus(Long userId, UserCoupon.UserCouponStatus status) {
        return userCouponJpaRepository.findByUserIdAndStatus(userId, status);
    }
}
