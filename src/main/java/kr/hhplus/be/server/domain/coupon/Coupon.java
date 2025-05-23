package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupon")
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(name = "coupon_name")
    private String name;

    @Column(name = "total_quantity")
    private Long totalCount;

    @Column(name = "issued_quantity")
    private Long issuedCount;

    @Column(name = "coupon_type")
    private DiscountType discountType;

    @Column(name = "discount_rate")
    private Long discountRate;

    @Column(name = "discount_amount")
    private Long discountAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;

    public Coupon(){
    }

    public enum DiscountType{
        RATE, AMOUNT;
    }

    public enum CouponStatus {
        ACTIVE, INACTIVE
    }

    @Builder
    public Coupon(String name, Long totalCount, Long issuedCount, DiscountType discountType, Long discountRate, Long discountAmount, CouponStatus status, LocalDateTime startDate, LocalDateTime endDate){
        this.name = name;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.discountAmount = discountAmount;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Coupon create(CouponCommand command){
        if (command.getDiscountType() == DiscountType.RATE) {
            if (command.getDiscountRate() == null) {
                throw new IllegalArgumentException(ErrorCode.DISCOUNT_RATE_NOT_FOUND.getMessage());
            }
        } else if (command.getDiscountType() == DiscountType.AMOUNT) {
            if (command.getDiscountAmount() == null) {
                throw new IllegalArgumentException(ErrorCode.DISCOUNT_AMOUNT_NOT_FOUND.getMessage());
            }
        } else {
            throw new IllegalArgumentException(ErrorCode.COUPON_TYPE_NOT_FOUND.getMessage());
        }

        return new Coupon(
                command.getName(),
                command.getTotalCount(),
                0L,
                command.getDiscountType(),
                command.getDiscountRate(),
                command.getDiscountAmount(),
                CouponStatus.ACTIVE,
                command.getStartDate(),
                command.getEndDate()
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
            return Math.round(originalAmount * (this.discountRate / 100.0));
        } else if (this.discountType == DiscountType.AMOUNT) {
            return this.discountAmount;
        } else {
            throw new IllegalStateException(ErrorCode.COUPON_TYPE_NOT_FOUND.getMessage());
        }
    }
}
