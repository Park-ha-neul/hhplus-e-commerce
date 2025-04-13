package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.user.UserPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCoupon extends BaseEntity {
    @Id
    private Long id;

    @ManyToOne
    private UserPoint user;

    @ManyToOne
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    public void use() {
        if (this.status == CouponStatus.USED) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }
        this.status = CouponStatus.USED;
    }
}
