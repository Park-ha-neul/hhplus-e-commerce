package kr.hhplus.be.server.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class CouponCommand {
    private String name;
    private Long totalCount;
    private Coupon.DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
