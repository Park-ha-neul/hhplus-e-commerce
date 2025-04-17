package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user_order")
@Builder
@AllArgsConstructor
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    private UserPointEntity userPointEntity;

    @OneToOne
    private UserCouponEntity userCouponEntity;

    @Enumerated(EnumType.STRING)
    private OrderStatus type; // pending, success, fail

    private OrderEntity(UserPointEntity userPointEntity, UserCouponEntity userCouponEntity, OrderStatus type) {
        this.userPointEntity = userPointEntity;
        this.userCouponEntity = userCouponEntity;
        this.type = type;
    }

    public static OrderEntity create(UserPointEntity userPointEntity, UserCouponEntity coupon) {
        return new OrderEntity(userPointEntity, coupon, OrderStatus.PENDING);
    }

    public void complete() {
        this.type = OrderStatus.SUCCESS;
    }

    public void cancel() {
        this.type = OrderStatus.FAIL;
    }

    public void addOrderItem(OrderItemEntity orderItemEntity) {
        this.orderItemEntities.add(orderItemEntity);
    }
}