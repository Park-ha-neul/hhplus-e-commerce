package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.domain.coupon.DiscountType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
