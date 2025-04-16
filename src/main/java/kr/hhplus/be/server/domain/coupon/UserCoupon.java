package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private User user;

    @ManyToOne
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private UserCoupon(User user, Coupon coupon, CouponStatus status){
        this.user = user;
        this.coupon = coupon;
        this.status = status;
    }

    public static UserCoupon issueTo(User user, Coupon coupon) {
        coupon.increaseIssuedCount();
        return new UserCoupon(user, coupon, CouponStatus.ISSUED);
    }

    public void use() {
        if (this.status == CouponStatus.USED) {
            throw new IllegalArgumentException(ErrorCode.ALREADY_USED_COUPON.getMessage());
        }
        this.status = CouponStatus.USED;
    }
}
