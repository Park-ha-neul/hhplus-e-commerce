package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

    private CouponRepository couponRepository;

    public Coupon create(CouponCreateRequest request){
        Coupon coupon = Coupon.create(request);
        return couponRepository.save(coupon);
    }

    public long calculateDiscount(UserCoupon userCoupon, Order order) {
        Coupon coupon = userCoupon.getCoupon();

        long totalAmount = order.getOrderItems().stream()
                .mapToLong(item -> item.getTotalPrice()).sum();

        return coupon.calculateDiscount(totalAmount);
    }
}
