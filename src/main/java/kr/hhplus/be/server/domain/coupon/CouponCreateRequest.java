package kr.hhplus.be.server.domain.coupon;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponCreateRequest {
    private String name;
    private Long totalCount;
    private DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
