package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user_coupon")
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
    public UserCoupon(Long userId, Long couponId){
        this.userId = userId;
        this.couponId = couponId;
        this.status = UserCouponStatus.ISSUED;
    }

    public void use() {
        if (this.status == UserCouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }
        this.status = UserCouponStatus.USED;
    }
}
