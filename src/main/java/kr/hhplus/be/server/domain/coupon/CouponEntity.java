package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "coupon")
public class CouponEntity extends BaseEntity {
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

    private CouponEntity(String name, Long totalCount, Long issuedCount, DiscountType dstype, Long discountRate, Long discountAmount, CouponStatus status, LocalDateTime startDate, LocalDateTime endDate){
        this.name = name;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.discountType = dstype;
        this.discountRate = discountRate;
        this.discountAmount = discountAmount;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CouponEntity create(CouponCreateRequest request){
        if (request.getDiscountType() == DiscountType.RATE) {
            if (request.getDiscountRate() == null) {
                throw new IllegalArgumentException(ErrorCode.DISCOUNT_RATE_NOT_FOUND.getMessage());
            }
        } else if (request.getDiscountType() == DiscountType.AMOUNT) {
            if (request.getDiscountAmount() == null) {
                throw new IllegalArgumentException(ErrorCode.DISCOUNT_AMOUNT_NOT_FOUND.getMessage());
            }
        } else {
            throw new IllegalArgumentException(ErrorCode.COUPON_TYPE_NOT_FOUND.getMessage());
        }

        return new CouponEntity(
                request.getName(),
                request.getTotalCount(),
                0L,
                request.getDiscountType(),
                request.getDiscountRate(),
                request.getDiscountAmount(),
                CouponStatus.ACTIVE,
                request.getStartDate(),
                request.getEndDate()
        );
    }

    public boolean isIssuable() {
        return (issuedCount != null ? issuedCount : 0L) < totalCount;
    }

    public void increaseIssuedCount() {
        if (!isIssuable()) {
            throw new IllegalArgumentException(ErrorCode.COUPON_ISSUED_EXCEED.getMessage());
        }
        this.issuedCount++;
    }

    public long calculateDiscount(long originalAmount) {
        if (this.discountType == DiscountType.RATE) {
            return originalAmount * this.discountRate / 100;
        } else if (this.discountType == DiscountType.AMOUNT) {
            return this.discountAmount;
        } else {
            throw new IllegalStateException(ErrorCode.COUPON_TYPE_NOT_FOUND.getMessage());
        }
    }
}
