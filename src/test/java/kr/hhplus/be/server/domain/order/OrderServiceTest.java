package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void 주문_등록(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);

        Order expectedOrder = Order.create(point, coupon);

        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        Order result = orderService.create(point, coupon);

        assertEquals(OrderType.PENDING, result.getType());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void 주문_조회_성공(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);

        Order expectedOrder = Order.create(point, coupon);

        when(orderRepository.findById(expectedOrder.getOrderId())).thenReturn(Optional.of(expectedOrder));

        Order result = orderService.getOrder(expectedOrder.getOrderId());

        assertEquals(OrderType.PENDING, result.getType());
        verify(orderRepository).findById(expectedOrder.getOrderId());
    }

    @Test
    void 주문_조회시_없는경우_예외처리(){
        Long orderId = 2L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrder(orderId);
        });

        assertEquals(ErrorCode.ORDER_NOT_FOUND.getMessage(), e.getMessage());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void 사용자_주문조회(){
        Long userId = 1L;
        List<Order> orders = List.of(mock(Order.class), mock(Order.class)); // 또는 실제 Order 객체

        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        List<Order> result = orderService.getOrderByUserId(userId);

        assertEquals(2, result.size());
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void 사용자_주문조회시_주문이_없는경우(){
        Long userId = 1L;
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderByUserId(userId);
        });

        assertEquals(ErrorCode.ORDER_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    void 주문_완료(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);

        Order expectedOrder = Order.create(point, coupon);

        when(orderRepository.findById(expectedOrder.getOrderId())).thenReturn(Optional.of(expectedOrder));
        orderService.completeOrder(expectedOrder.getOrderId());

        assertEquals(OrderType.SUCCESS, expectedOrder.getType());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void 주문_취소(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);

        Order expectedOrder = Order.create(point, coupon);

        when(orderRepository.findById(expectedOrder.getOrderId())).thenReturn(Optional.of(expectedOrder));
        orderService.cancleOrder(expectedOrder.getOrderId());

        assertEquals(OrderType.FAIL, expectedOrder.getType());
        verify(orderRepository).save(any(Order.class));
    }
}
