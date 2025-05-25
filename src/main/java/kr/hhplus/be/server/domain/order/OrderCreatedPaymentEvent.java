package kr.hhplus.be.server.domain.order;

import lombok.Getter;

@Getter
public class OrderCreatedPaymentEvent {
    private final Long orderId;

    public OrderCreatedPaymentEvent(Long orderId) {
        this.orderId = orderId;
    }
}
