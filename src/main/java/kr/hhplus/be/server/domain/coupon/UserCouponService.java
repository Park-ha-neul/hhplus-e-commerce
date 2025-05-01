package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public UserCouponResult issue(Long userId, Long couponId){
        User user = userRepository.findById(userId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        if(coupon.getStatus() != Coupon.CouponStatus.ACTIVE){
            throw new IllegalArgumentException(ErrorCode.INACTIVE_COUPON.getMessage());
        }

        if(coupon.getIssuedCount() >=  coupon.getTotalCount()){
            throw new IllegalArgumentException(ErrorCode.COUPON_ISSUED_EXCEED.getMessage());
        }

        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);
        coupon.increaseIssuedCount();

        return UserCouponResult.of(userCoupon);
    }

    public List<UserCouponResult> getUserCoupons(Long userId, UserCoupon.UserCouponStatus status){
        if(status == null){
            List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
            return userCoupons.stream()
                    .map(UserCouponResult::of)
                    .collect(Collectors.toList());
        } else{
            List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndStatus(userId, status);
            return userCoupons.stream()
                    .map(UserCouponResult::of)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
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
