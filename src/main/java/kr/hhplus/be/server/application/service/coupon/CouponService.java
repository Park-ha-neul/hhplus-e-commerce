package kr.hhplus.be.server.application.service.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCreateRequest;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Coupon createCoupon(CouponCreateRequest request) {
        Coupon coupon = Coupon.createFrom(request);
        return couponRepository.save(coupon);
    }
}
