package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import kr.hhplus.be.server.support.CustomBadRequestException;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private CouponRepository couponRepository;
    private UserRepository userRepository;
    private UserCouponRepository userCouponRepository;

    public List<Coupon> getCoupons(Coupon.CouponStatus status){
        if (status != null){
            return couponRepository.findAllByStatus(status);
        } else {
            return couponRepository.findAllCoupons();
        }
    }

    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));
    }

    public Coupon create(CouponCreateRequest request, Long userId){
        User user = userRepository.findById(userId);
        if(!user.isAdmin()){
            throw new ForbiddenException(ErrorCode.CREATE_COUPON_MUST_BE_ADMIN.getMessage());
        }

        try {
            Coupon coupon = Coupon.create(request);
            return couponRepository.save(coupon);
        } catch (IllegalArgumentException e) {
            throw new CustomBadRequestException(e.getMessage());
        }
    }

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
