package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
        User user = findUser(command.getUserId());
        UserCoupon userCoupon = findUserCoupon(command.getUserCouponId());

        Order order = createOrder(user, userCoupon, command.getOrderItems());
        long payAmount = calculatePayAmount(order, userCoupon);

        orderRepository.save(order);
        publishOrderCreatedEvent(order, payAmount);

        return OrderResult.of(order);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId);
    }

    private UserCoupon findUserCoupon(Long couponId) {
        return userCouponRepository.findById(couponId);
    }

    private Order createOrder(User user, UserCoupon coupon, List<OrderItemCommand> itemCommands) {
        Order order = new Order(user.getUserId(), coupon.getCouponId());
        for (OrderItemCommand cmd : itemCommands) {
            order.addOrderItem(new OrderItem(order, cmd.getProductId(), cmd.getQuantity(), cmd.getPrice()));
        }
        return order;
    }

    private long calculatePayAmount(Order order, UserCoupon userCoupon) {
        long totalAmount = order.getItems().stream()
                .mapToLong(item -> item.getUnitPrice() * item.getQuantity())
                .sum();

        long couponDiscount = couponRepository.findById(order.getCouponId())
                .map(coupon -> coupon.calculateDiscount(totalAmount))
                .orElse(0L);

        return totalAmount - couponDiscount;
    }

    private void publishOrderCreatedEvent(Order order, long payAmount) {
        eventPublisher.publish(
                new OrderCreatedStockDeductEvent(order.getItems()),
                new OrderCreatedPointUsedEvent(order.getUserId(), payAmount),
                new OrderCreatedCouponUsedEvent(order.getCouponId()),
                new OrderCreatedPaymentEvent(order.getOrderId())
        );
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
