package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    public Order create(UserPoint point, UserCoupon coupon){

        Order order = Order.create(point, coupon);
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.ORDER_NOT_FOUND.getMessage()));
    }

    public List<Order> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new IllegalArgumentException(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }

        return orders;
    }

    public void completeOrder(Long orderId){
        Order order = getOrder(orderId);
        order.complete();
        orderRepository.save(order);
    }

    public void cancleOrder(Long orderId){
        Order order = getOrder(orderId);
        order.cancle();
        orderRepository.save(order);
    }
}
