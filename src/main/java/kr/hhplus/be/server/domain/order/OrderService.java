package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public OrderResult create(OrderCommand command){
        User user = userRepository.findById(command.getUserId());
        Optional<UserCoupon> userCoupon = userCouponRepository.findById(command.getUserCouponId());


        Order order = new Order(user.getUserId(), userCoupon.get().getCouponId());

        for (OrderItemCommand orderItemCommand : command.getOrderItems()) {
            OrderItem orderItem = new OrderItem(order, orderItemCommand.getProductId(), orderItemCommand.getQuantity(), orderItemCommand.getPrice());
            order.addOrderItem(orderItem);
        }

        long totalAmount = command.getOrderItems().stream()
                .mapToLong(item -> item.getPrice() * item.getQuantity())
                .sum();

        long couponDiscount = userCoupon
                .flatMap(uc -> couponRepository.findById(order.getCouponId()))
                .map(coupon -> coupon.calculateDiscount(totalAmount))
                .orElse(0L);

        long payAmount = totalAmount - couponDiscount;

        orderRepository.save(order);

        eventPublisher.publishOrderCreatedEvent(new OrderCreatedEvent(
                order.getOrderId(),
                order.getCouponId(),
                order.getUserId(),
                payAmount,
                order.getItems(),
                LocalDateTime.now())
        );

        return OrderResult.of(order);
    }

    public Page<Order> getOrders(Order.OrderStatus status, Pageable pageable){
        if(status == null){
            return orderRepository.findAll(pageable);
        } else{
            return orderRepository.findByStatus(status, pageable);
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
