package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public UserCoupon issue(Long userId, Long couponId){
        User user = userRepository.findById(userId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        if(coupon.getStatus() != Coupon.CouponStatus.ACTIVE){
            throw new IllegalArgumentException(ErrorCode.INACTIVE_COUPON.getMessage());
        }

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCouponRepository.save(userCoupon);
        coupon.increaseIssuedCount();

        return userCoupon;
    }

    public UserCoupon getUserCoupon(Long userCouponId){
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));
    }

    public List<UserCoupon> getUserCoupons(Long userId){
        User user = userRepository.findById(userId);
        return userCouponRepository.findByUserId(userId);
    }

    public void use(Long userCouponId){
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));

        if (userCoupon.getStatus() == UserCoupon.UserCouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }

        userCoupon.use();

        userCouponRepository.save(userCoupon);
    }
}
