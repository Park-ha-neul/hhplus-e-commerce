package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    private String name;
    private Long totalCount;
    private Long issuedCount;

    private DiscountType discountType;
    private Long discountRate;
    private Long discountAmount;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean isIssuable() {
        return (issuedCount != null ? issuedCount : 0L) < totalCount;
    }

    public void increaseIssuedCount() {
        if (!isIssuable()) {
            throw new IllegalArgumentException("쿠폰 발급 한도를 초과했습니다.");
        }
        this.issuedCount++;
    }
    public static Coupon createFrom(CouponCreateRequest request) {
        Coupon coupon = new Coupon();

        coupon.setName(request.getName());
        coupon.setTotalCount(request.getTotalCount());
        coupon.setIssuedCount(0L);
        coupon.setDiscountType(request.getDiscountType());

        if (request.getDiscountType() == DiscountType.RATE) {
            if (request.getDiscountRate() == null) {
                throw new IllegalArgumentException("할인율이 필요합니다.");
            }
            coupon.setDiscountRate(request.getDiscountRate());
            coupon.setDiscountAmount(null);
        } else if (request.getDiscountType() == DiscountType.AMOUNT) {
            if (request.getDiscountAmount() == null) {
                throw new IllegalArgumentException("할인금액이 필요합니다.");
            }
            coupon.setDiscountAmount(request.getDiscountAmount());
            coupon.setDiscountRate(null);
        } else {
            throw new IllegalArgumentException("유효하지 않은 할인 타입입니다.");
        }

        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setStatus(CouponStatus.ACTIVE);
        return coupon;
    }

    public long calculateDiscount(long originalAmount) {
        if (this.discountType == DiscountType.RATE) {
            return originalAmount * this.discountRate / 100;
        } else if (this.discountType == DiscountType.AMOUNT) {
            return this.discountAmount;
        } else {
            throw new IllegalStateException("유효하지 않은 할인 타입입니다.");
        }
    }
}
