package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user_coupon", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_status", columnList = "status")
})
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    public enum UserCouponStatus {
        ISSUED, USED, EXPIRED
    }

    @Builder
    public UserCoupon(Long userId, Long couponId, UserCouponStatus status){
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
    }

    protected UserCoupon() {
    }

    public static UserCoupon create(Long userId, Long couponId){
        return new UserCoupon(
                userId,
                couponId,
                UserCouponStatus.ISSUED
        );
    }

    public void use() {
        if (this.status == UserCouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }
        this.status = UserCouponStatus.USED;
    }
}
