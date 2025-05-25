package kr.hhplus.be.server.domain.order;

import lombok.Getter;

@Getter
public class OrderCreatedPointUsedEvent {
    private final Long userId;
    private final Long totalAmount;

    public OrderCreatedPointUsedEvent(Long userId, Long totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}
