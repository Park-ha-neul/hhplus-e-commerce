package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.PopularProductService;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserPointService userPointService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @Mock
    private PopularProductService popularProductService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Test
    void 결제_성공_처리() {
        Long orderId = 2L;
        Long userId = 3L;
        Long totalAmount = 3000L;

        Order order = mock(Order.class);
        OrderItem item1 = mock(OrderItem.class);
        OrderItem item2 = mock(OrderItem.class);

        when(order.getUserId()).thenReturn(userId);
        when(order.getItems()).thenReturn(List.of(item1, item2));

        when(item1.getProductId()).thenReturn(100L);
        when(item1.getQuantity()).thenReturn(2L);
        when(item2.getProductId()).thenReturn(200L);
        when(item2.getQuantity()).thenReturn(1L);

        when(orderService.getOrder(orderId)).thenReturn(order);
        doNothing().when(userPointService).use(userId, totalAmount);

        // when
        Payment result = paymentFacade.processPayment(orderId, totalAmount);

        // then
        verify(userPointService).use(userId, totalAmount);
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
        verify(order).complete();
        verify(popularProductService).incrementProductScore(100L, 2L);
        verify(popularProductService).incrementProductScore(200L, 1L);
    }

    @Test
    void 결제_실패_처리_포인트부족() {
        // given
        Long orderId = 2L;
        Long userId = 3L;
        Long totalAmount = 1000L;

        Order order = mock(Order.class);

        when(order.getUserId()).thenReturn(userId);
        when(orderService.getOrder(orderId)).thenReturn(order);

        // simulate failure when using points
        doThrow(new IllegalArgumentException("포인트가 부족합니다"))
                .when(userPointService).use(userId, totalAmount);

        // when
        Payment result = paymentFacade.processPayment(orderId, totalAmount);

        // then
        assertEquals(Payment.PaymentStatus.FAIL, result.getStatus());
        assertEquals("포인트가 부족합니다", result.getFailureReason());

        verify(order).fail();
    }
}
