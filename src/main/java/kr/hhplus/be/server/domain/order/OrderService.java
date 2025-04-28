package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductRepository productRepository;

    public OrderResult create(OrderCommand command){
        User user = userRepository.findById(command.getUserId());
        Optional<UserCoupon> userCoupon = userCouponRepository.findById(command.getUserCouponId());

        Order order = new Order(user.getUserId(), userCoupon.get().getCouponId());

        for (OrderItemCommand orderItemCommand : command.getOrderItems()) {
            Product product = productRepository.findById(orderItemCommand.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));

            if(product.getQuantity() < orderItemCommand.getQuantity()){
                throw new IllegalArgumentException(ProductErrorCode.NOT_ENOUGH_STOCK.getMessage());
            }
            product.decreaseBalance(orderItemCommand.getQuantity());
            OrderItem orderItem = new OrderItem(order, orderItemCommand.getProductId(), orderItemCommand.getQuantity(), product.getPrice());
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);
        order.complete();
        return OrderResult.of(order);
    }

    public List<Order> getOrders(Order.OrderStatus status){
        if(status == null){
            return orderRepository.findAll();
        } else{
            return orderRepository.findByStatus(status);
        }
    }

    public Order getOrder(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorCode.ORDER_NOT_FOUND.getMessage()));
    }

    public List<Order> getUserOrders(Long userId, Order.OrderStatus status) {
        if(status == null){
            return orderRepository.findByUserId(userId);
        } else{
            return orderRepository.findByUserIdAndStatus(userId, status);
        }
    }

    public void cancel(Long orderId){
        Order order = getOrder(orderId);
        order.cancel();
        orderRepository.save(order);
    }
}
