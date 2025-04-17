package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import kr.hhplus.be.server.domain.user.UserPointErrorCode;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import kr.hhplus.be.server.support.CustomBadRequestException;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private CouponEntityRepository couponEntityRepository;
    private UserPointEntityRepository userPointEntityRepository;
    private UserCouponEntityRepository userCouponEntityRepository;

    public List<CouponEntity> getCoupons(CouponStatus status){
        if (status != null){
            return couponEntityRepository.findAllByStatus(status);
        } else {
            return couponEntityRepository.findAll();
        }
    }

    public CouponEntity getCoupon(Long couponId){
        return couponEntityRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));
    }

    public CouponEntity create(CouponCreateRequest request, Long userId){
        UserPointEntity userPointEntity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        if(!userPointEntity.getUser().isAdmin()){
            throw new ForbiddenException(ErrorCode.CREATE_COUPON_MUST_BE_ADMIN.getMessage());
        }

        try {
            CouponEntity coupon = CouponEntity.create(request);
            return couponEntityRepository.save(coupon);
        } catch (IllegalArgumentException e) {
            throw new CustomBadRequestException(e.getMessage());
        }
    }

    @Transactional
    public UserCouponEntity issueCouponToUser(Long userId, Long couponId){
        UserPointEntity user = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        CouponEntity coupon = couponEntityRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        if(coupon.getStatus() != CouponStatus.ACTIVE){
            throw new IllegalArgumentException(ErrorCode.INACTIVE_COUPON.getMessage());
        }

        UserCouponEntity userCouponEntity = UserCouponEntity.create(user, coupon);
        userCouponEntityRepository.save(userCouponEntity);

        coupon.increaseIssuedCount();
        couponEntityRepository.save(coupon);

        return userCouponEntity;
    }

    public List<UserCouponEntity> getUserCoupons(Long userId){
        UserPointEntity userPointEntity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        return userCouponEntityRepository.findByUserId(userId);
    }


    public void useCoupon(Long userCouponId){
        UserCouponEntity userCouponEntity = userCouponEntityRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.USER_COUPON_NOT_FOUND.getMessage()));

        if (userCouponEntity.getStatus() == UserCouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }

        userCouponEntity.use();

        userCouponEntityRepository.save(userCouponEntity);
    }
}
