package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResult {
    private Long id;
    private Long userId;
    private List<OrderItem> item;
    private Long userCouponId;
    private Order.OrderStatus status;

    public static OrderResult of(Order order) {
        return new OrderResult(
                order.getOrderId(),
                order.getUserId(),
                order.getItems(),
                order.getCouponId(),
                order.getStatus()
        );
    }
}
