package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.coupon.ErrorCode;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COUPON_NOT_FOUND.getMessage()));
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
