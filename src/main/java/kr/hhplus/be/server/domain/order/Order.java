package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "user_order")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Column(name = "user_coupon_id")
    private Long couponId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public enum OrderStatus{
        PENDING, SUCCESS, FAIL, CANCELED;
    }

    @Builder
    public Order(Long userId, Long couponId){
        this.userId = userId;
        this.couponId = couponId;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
    }

    public void complete() {
        this.status = OrderStatus.SUCCESS;
    }

    public void fail() {
        this.status = OrderStatus.FAIL;
    }

    public void cancel(){this.status = OrderStatus.CANCELED;}

    public void addOrderItem(OrderItem orderItem) {
        this.items.add(orderItem);
    }
}