package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CouponCreateRequest {
    private String name;
    private Long totalCount;
    private Coupon.DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public CouponCommand toCommand(){
        return new CouponCommand(
                this.getName(),
                this.getTotalCount(),
                this.getDiscountType(),
                this.getDiscountRate(),
                this.getDiscountAmount(),
                this.getStartDate(),
                this.getEndDate()
        );
    }
}
