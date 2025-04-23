package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponCreateRequest {
    private String name;
    private Long totalCount;
    private Coupon.DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
