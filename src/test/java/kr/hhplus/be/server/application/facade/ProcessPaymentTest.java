package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessPaymentTest {

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

    @InjectMocks
    private ProcessPayment processPayment;

    @Test
    void 결제_성공_처리() {
        Long paymentId = 1L;
        Long orderId = 2L;
        Long userId = 3L;

        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);

        when(payment.getOrderId()).thenReturn(orderId);
        when(payment.getTotalAmount()).thenReturn(1000L);
        when(order.getUserId()).thenReturn(userId);
        when(paymentService.getPayment(paymentId)).thenReturn(payment);
        when(orderService.getOrder(orderId)).thenReturn(order);
        when(couponService.calculateDiscount(anyLong(), anyLong())).thenReturn(200L);

        doNothing().when(userCouponService).use(anyLong());
        doNothing().when(userPointService).use(userId, 800L);

        Payment result = processPayment.processPayment(paymentId);

        verify(userPointService).use(userId,800L);
        verify(payment).complete();
        verify(order).complete();
        assertEquals(payment, result);
    }

    @Test
    void 결제_실패_처리_포인트부족() {
        Long paymentId = 1L;
        Long orderId = 2L;
        Long userId = 3L;

        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);

        when(payment.getOrderId()).thenReturn(orderId);
        when(payment.getTotalAmount()).thenReturn(1000L);  // Assuming total amount is 1000
        when(order.getUserId()).thenReturn(userId);

        when(paymentService.getPayment(paymentId)).thenReturn(payment);
        when(orderService.getOrder(orderId)).thenReturn(order);
        when(couponService.calculateDiscount(anyLong(), anyLong())).thenReturn(0L); // No discount

        doThrow(new IllegalArgumentException("포인트가 부족합니다")).when(userPointService).use(userId, 1000L);  // Trying to deduct 1000 points

        Payment result = processPayment.processPayment(paymentId);

        verify(payment).fail("포인트가 부족합니다");
        verify(order).fail();
    }
}
