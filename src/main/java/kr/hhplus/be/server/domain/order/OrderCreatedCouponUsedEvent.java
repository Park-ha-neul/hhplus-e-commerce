package kr.hhplus.be.server.domain.order;

import lombok.Getter;

@Getter
public class OrderCreatedCouponUsedEvent {
    private final Long couponId;

    public OrderCreatedCouponUsedEvent(Long couponId) {
        this.couponId = couponId;
    }
}
