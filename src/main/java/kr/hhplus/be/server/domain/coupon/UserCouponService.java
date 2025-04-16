package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponService {
    private UserCouponRepository userCouponRepository;

    public UserCoupon getUserCouponById(Long userCouponId){
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));
    }

    public List<UserCoupon> getUserCoupon(Long userId) {
        return userCouponRepository.findByUserAndCoupon(userId);
    }

    public void createUserCoupon(User user, Coupon coupon){
        UserCoupon userCoupon = UserCoupon.issueTo(user, coupon);
        userCouponRepository.save(userCoupon);
    };

    public void useCoupon(Long userCouponId){
        UserCoupon userCoupon = getUserCouponById(userCouponId);
        userCoupon.use();
        userCouponRepository.save(userCoupon);
    }


}
