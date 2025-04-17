package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "coupon")
@Builder
@AllArgsConstructor
public class UserCouponEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @ManyToOne
    private UserPointEntity userPointEntity;

    @ManyToOne
    private CouponEntity couponEntity;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    private UserCouponEntity(UserPointEntity userPointEntity, CouponEntity couponEntity, UserCouponStatus status){
        this.userPointEntity = userPointEntity;
        this.couponEntity = couponEntity;
        this.status = status;
    }

    public static UserCouponEntity create(UserPointEntity userPointEntity, CouponEntity couponEntity){
        return new UserCouponEntity(
                userPointEntity,
                couponEntity,
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
