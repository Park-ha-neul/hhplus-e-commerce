package kr.hhplus.be.server.application.facade;

import lombok.Getter;

@Getter
public class PaymentCompletedPopularProductEvent {
    private final Long orderId;

    public PaymentCompletedPopularProductEvent(Long orderId) {
        this.orderId = orderId;
    }
}
