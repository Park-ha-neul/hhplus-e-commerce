package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository{
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findAll();
}
