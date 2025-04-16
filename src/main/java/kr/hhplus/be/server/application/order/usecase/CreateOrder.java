package kr.hhplus.be.server.application.order.usecase;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.user.UserPoint;

import java.util.List;

public class CreateOrder {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public CreateOrder(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    public Order execute(UserPoint point, UserCoupon coupon, List<OrderItemRequest> orderItemRequests) {
        Order order = orderService.create(point, coupon);

        for (OrderItemRequest request : orderItemRequests) {
            OrderItem orderItem = orderItemService.createOrderItem(order, request.getProduct(), request.getQuantity(), request.getPrice());
        }

        return order;
    }

}
