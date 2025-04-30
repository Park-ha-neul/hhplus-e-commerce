package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<OrderResult> from (List<Order> orderList){
        return orderList.stream()
                .map(OrderResult::of)
                .collect(Collectors.toList());
    }
}
