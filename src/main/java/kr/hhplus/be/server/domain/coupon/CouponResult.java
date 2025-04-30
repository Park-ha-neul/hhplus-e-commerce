package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CouponResult {
    private Long id;
    private String name;
    private Long totalCount;
    private Coupon.DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private Coupon.CouponStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CouponResult of(Coupon coupon) {
        return new CouponResult(
                coupon.getCouponId(),
                coupon.getName(),
                coupon.getTotalCount(),
                coupon.getDiscountType(),
                coupon.getDiscountRate(),
                coupon.getDiscountAmount(),
                coupon.getStatus(),
                coupon.getStartDate(),
                coupon.getEndDate()
        );
    }
}
