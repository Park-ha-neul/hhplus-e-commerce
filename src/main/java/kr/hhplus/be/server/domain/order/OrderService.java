package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private UserCouponRepository userCouponRepository;
    private ProductRepository productRepository;

    public Order create(OrderReqeust request){
        User user = userRepository.findById(request.getUserId());
        UserCoupon userCoupon = userCouponRepository.findByUserAndCoupon(request.getUserId(), request.getCouponId());

        Order order = new Order(user.getUserId(), userCoupon.getUserCouponId());

        for (OrderItemRequest orderItemRequest : request.getOrderItems()) {
            Product product = productRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));
            OrderItem orderItem = new OrderItem(order, orderItemRequest.getProductId(), orderItemRequest.getQuantity(), product.getPrice());
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);
        order.complete();
        return order;
    }

    public List<Order> getOrders(Order.OrderStatus status){
        List<Order> orders = orderRepository.findByStatus(status);
        return orders;
    }

    public Order getOrder(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorCode.ORDER_NOT_FOUND.getMessage()));
    }

    public List<Order> getOrderByUserId(Long userId) {
        List<Order> result = orderRepository.findByUserId(userId);
        return result;
    }

    public void cancel(Long orderId){
        Order order = getOrder(orderId);
        order.cancel();
    }
}
