package kr.hhplus.be.server.domain.order;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreatedStockDeductEvent {
    private final List<OrderItem> items;

    public OrderCreatedStockDeductEvent(List<OrderItem> items) {
        this.items = items;
    }
}
