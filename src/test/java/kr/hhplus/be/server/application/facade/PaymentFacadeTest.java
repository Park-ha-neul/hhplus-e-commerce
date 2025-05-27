package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.PopularProductService;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Mock
    private PaymentCompletedEventPublisher eventPublisher;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Test
    void 결제_성공시_payment와_order가_완료되고_이벤트가_발행된다() {
        // given
        Long orderId = 1L;
        Long totalAmount = 10000L;

        Order order = mock(Order.class);
        when(orderService.getOrder(orderId)).thenReturn(order);

        // when
        Payment payment = paymentFacade.processPayment(orderId, totalAmount);

        // then
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.COMPLETED);
        verify(order).complete();

        // 이벤트 확인
        ArgumentCaptor<Object[]> eventCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(eventPublisher).publish(eventCaptor.capture());

        Object[] publishedEvents = eventCaptor.getValue();
        assertThat(publishedEvents).hasSize(2);
        assertThat(Arrays.stream(publishedEvents)).anyMatch(e -> e instanceof PaymentCompletedPopularProductEvent);
        assertThat(Arrays.stream(publishedEvents)).anyMatch(e -> e instanceof PaymentCompletedExternalPlatformEvent);
    }

    @Test
    void 결제_실패시_payment와_order가_실패상태로_변경되고_이벤트가_발행되지_않는다() {
        // given
        Long orderId = 1L;
        Long totalAmount = 10000L;

        Order order = mock(Order.class);
        when(orderService.getOrder(orderId)).thenReturn(order);

        // order.complete() 호출 시 예외 발생하도록 설정
        doThrow(new RuntimeException("결제 실패")).when(order).complete();

        // when
        Payment payment = paymentFacade.processPayment(orderId, totalAmount);

        // then
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.FAIL);
        verify(order).fail();
        verify(eventPublisher, never()).publish(any());
    }
}
