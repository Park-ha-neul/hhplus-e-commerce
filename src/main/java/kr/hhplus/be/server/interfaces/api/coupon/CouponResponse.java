package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long id;
    private String name;
    private Long totalCount;
    private Coupon.DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static CouponResponse from(CouponResult result) {
        return CouponResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .totalCount(result.getTotalCount())
                .discountType(result.getDiscountType())
                .discountRate(result.getDiscountRate())
                .discountAmount(result.getDiscountAmount())
                .startDate(result.getStartDate())
                .endDate(result.getEndDate())
                .build();
    }

    public static List<CouponResponse> from(List<CouponResult> resultList){
        return resultList.stream()
                .map(CouponResponse::from)
                .collect(Collectors.toList());
    }
}
