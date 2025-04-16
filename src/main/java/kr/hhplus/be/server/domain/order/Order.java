package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.UserPoint;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Embedded
    private UserPoint userPoint;

    @OneToOne
    private UserCoupon userCoupon;

    @Enumerated(EnumType.STRING)
    private OrderType type; // pending, success, fail

    private Order(UserPoint userPoint, UserCoupon userCoupon, OrderType type) {
        this.userPoint = userPoint;
        this.userCoupon = userCoupon;
        this.type = type;
    }

    public static Order create(UserPoint point, UserCoupon coupon) {
        return new Order(point, coupon, OrderType.PENDING);
    }

    public void complete() {
        this.type = OrderType.SUCCESS;
    }

    public void cancle() {
        this.type = OrderType.FAIL;
    }
}