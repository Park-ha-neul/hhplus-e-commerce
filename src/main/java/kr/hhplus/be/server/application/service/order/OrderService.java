package kr.hhplus.be.server.application.service.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.common.OrderType;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private OrderItemService orderItemService;

    public Order getOrderById(Long orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    public List<Order> getOrderByUserId(Long userId){
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(OrderDto orderDto){
        // 주문 엔티티 생성
        Order order = new Order();

        // 2. UserPoint 조회
        UserPoint userPoint = userPointRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        order.setUserPoint(userPoint);
        order.setType(OrderType.PENDING);

        // 주문 저장
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderType status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 주문이 없습니다."));

        order.setType(status);
        orderRepository.save(order);
    }
}
