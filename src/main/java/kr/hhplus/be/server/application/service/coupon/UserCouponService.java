package kr.hhplus.be.server.application.service.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Transactional
    public void useCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정보를 찾을 수 없습니다."));
        userCoupon.use();
    }

    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        UserPoint user = userPointRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // 1. 중복 발급 체크
        boolean alreadyIssued = userCouponRepository.existsByUserAndCoupon(user, coupon);
        if (alreadyIssued) {
            throw new IllegalArgumentException("이미 발급된 쿠폰입니다.");
        }

        // 3. 발급 한도 체크
        coupon.increaseIssuedCount(); // 내부에서 초과 시 예외 발생

        // 4. 발급 처리
        UserCoupon userCoupon = new UserCoupon(null, user, coupon, CouponStatus.ISSUED);
        userCouponRepository.save(userCoupon);
    }
}
