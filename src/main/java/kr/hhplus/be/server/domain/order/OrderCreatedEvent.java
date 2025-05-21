package kr.hhplus.be.server.domain.order;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderCreatedEvent {
    private final Long orderId;
    private final Long couponId;
    private final Long userId;
    private final Long totalAmount;
    private final List<OrderItem> items;
    private final LocalDateTime orderedAt;

    public OrderCreatedEvent(Long orderId, Long couponId, Long userId, Long totalAmount, List<OrderItem> items, LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.couponId = couponId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
        this.orderedAt = orderedAt;
    }
}
