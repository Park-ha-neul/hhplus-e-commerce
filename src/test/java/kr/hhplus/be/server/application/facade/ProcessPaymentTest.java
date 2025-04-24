package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.UserWithPointResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessPaymentTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private CouponService couponService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProcessPayment processPayment;

    @Test
    void 결제_성공_처리() {
        Long paymentId = 1L;
        Long orderId = 2L;
        Long userId = 3L;

        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);
        Coupon coupon = mock(Coupon.class);
        CouponResult couponResult = mock(CouponResult.class);
        UserPoint point = mock(UserPoint.class);

        List<OrderItem> items = List.of(new OrderItem(order, 1L, 1L, 500L));  // 총 1000원
        when(payment.getOrderId()).thenReturn(orderId);
        when(order.getUserId()).thenReturn(userId);
        when(order.getItems()).thenReturn(items);
        when(coupon.calculateDiscount(anyLong())).thenReturn(200L);

        when(paymentService.getPayment(paymentId)).thenReturn(payment);
        when(orderService.getOrder(orderId)).thenReturn(order);
        when(couponService.getCoupon(any())).thenReturn(couponResult);

        Payment result = processPayment.processPayment(paymentId);

        verify(point).use(300L); // 1000 - 200
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
        Coupon coupon = mock(Coupon.class);
        CouponResult couponResult = mock(CouponResult.class);
        UserPoint point = mock(UserPoint.class);

        List<OrderItem> items = List.of(new OrderItem(order, 1L, 1L, 500L)); // 총 1000원
        when(payment.getOrderId()).thenReturn(orderId);
        when(order.getUserId()).thenReturn(userId);
        when(order.getItems()).thenReturn(items);
        when(coupon.calculateDiscount(anyLong())).thenReturn(0L);

        when(paymentService.getPayment(paymentId)).thenReturn(payment);
        when(orderService.getOrder(orderId)).thenReturn(order);
        when(couponService.getCoupon(any())).thenReturn(couponResult);

        // 포인트 부족 예외
        doThrow(new IllegalArgumentException("포인트가 부족합니다")).when(point).use(anyLong());

        Payment result = processPayment.processPayment(paymentId);

        verify(payment).fail("포인트가 부족합니다");
        verify(order).fail();
    }
}
