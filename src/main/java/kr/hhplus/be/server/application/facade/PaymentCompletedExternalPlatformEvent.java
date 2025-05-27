package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.OrderItem;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PaymentCompletedExternalPlatformEvent {
    private final Long orderId;
    private final Long userId;
    private final List<OrderItem> orderItems;
    private final LocalDateTime updatedDate;

    public PaymentCompletedExternalPlatformEvent(Long orderId, Long userId, List<OrderItem> orderItems, LocalDateTime updatedDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.updatedDate = updatedDate;
    }
}
