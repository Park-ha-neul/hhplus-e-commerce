package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.CustomBadRequestException;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<CouponResult> getCoupons(Coupon.CouponStatus status){
        if (status != null){
            List<Coupon> coupons = couponRepository.findAllByStatus(status);
            return coupons.stream()
                    .map(CouponResult::of)
                    .collect(Collectors.toList());
        } else {
            List<Coupon> coupons = couponRepository.findAllCoupons();
            return coupons.stream()
                    .map(CouponResult::of)
                    .collect(Collectors.toList());
        }
    }

    public CouponResult getCoupon(Long couponId){
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        return CouponResult.of(coupon);
    }

    @Transactional
    public CouponResult create(CouponCommand command, Long userId){
        User user = userRepository.findById(userId);
        if(!user.isAdmin()){
            throw new ForbiddenException(ErrorCode.CREATE_COUPON_MUST_BE_ADMIN.getMessage());
        }

        try {
            Coupon coupon = Coupon.create(command);
            couponRepository.save(coupon);

            long stock = command.getTotalCount();
            String redisKey = "coupon:" + coupon.getCouponId();
            for (int i = 0; i < stock; i++) {
                redisTemplate.opsForList().leftPush(redisKey, "1");
            }

            // TTL 설정
            long secondsToExpire = Duration.between(LocalDateTime.now(), command.getEndDate()).getSeconds();
            if (secondsToExpire > 0) {
                redisTemplate.expire(redisKey, secondsToExpire, TimeUnit.SECONDS);
            }

            return CouponResult.of(coupon);
        } catch (IllegalArgumentException e) {
            throw new CustomBadRequestException(e.getMessage());
        }
    }

    public Long calculateDiscount(Long couponId, Long totalAmount){
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));
        return coupon.calculateDiscount(totalAmount);
    }
}
