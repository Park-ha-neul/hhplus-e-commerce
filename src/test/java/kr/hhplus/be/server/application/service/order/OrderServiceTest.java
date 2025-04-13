package kr.hhplus.be.server.application.service.order;

import kr.hhplus.be.server.domain.order.OrderType;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDto;
import kr.hhplus.be.server.domain.order.OrderItemDto;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void 주문_생성_성공(){
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 1000L, false);
        OrderDto orderDto = new OrderDto(userId, new ArrayList<>());

        // 주문 항목 추가
        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;
        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderDto.getOrderItems().add(orderItemDto);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        // when
        Order order = orderService.createOrder(orderDto);

        //then
        assertEquals(userPoint, order.getUserPoint());
        assertEquals(OrderType.PENDING, order.getType());
        verify(orderRepository, times(1)).save(order); // 주문이 저장되었는지 확인
    }

    @Test
    public void 주문_생성시_유효한_사용자없음_예외처리(){
        Long userId = 1L;
        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());
        OrderDto orderDto = new OrderDto(userId, new ArrayList<>());

        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;
        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderDto.getOrderItems().add(orderItemDto);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderDto);
        });

        assertEquals("유저 정보가 없습니다.", e.getMessage());
    }

    @Test
    public void 주문_상태_업데이트(){
        Long orderId = 1L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);
        Long couponId = 1L;
        OrderType type = OrderType.PENDING;
        Order order = new Order();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(orderId, OrderType.FAIL);

        assertEquals(OrderType.FAIL, order.getType());
        verify(orderRepository).save(order);
    }

    @Test
    public void 주문_상태_업데이트시_주문이_유효하지_않은경우_예외처리(){
        Long orderId = 1L;
        OrderType type = OrderType.SUCCESS;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(orderId, type);
        });

        assertEquals("해당하는 주문이 없습니다.", e.getMessage());
    }
}
