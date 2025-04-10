package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.service.order.OrderItemService;
import kr.hhplus.be.server.application.service.order.OrderService;
import kr.hhplus.be.server.application.service.product.ProductService;
import kr.hhplus.be.server.application.service.userPoint.UserService;
import kr.hhplus.be.server.domain.common.OrderType;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDto;
import kr.hhplus.be.server.domain.order.OrderItemDto;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOrderFacadeService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrderWithItems(OrderDto orderDto){
        UserPoint userPoint = userService.getUser(orderDto.getUserId());

        Order order = orderService.createOrder(orderDto);

        for(OrderItemDto orderItemDto : orderDto.getOrderItems()){
            try{
                orderItemService.createOrderItem(orderItemDto, order);
            } catch (IllegalArgumentException e){
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        orderService.updateOrderStatus(order.getOrderId(), OrderType.SUCCESS);
        return order;
    }
}
