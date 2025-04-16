package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCreateRequest;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProductService;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderPaymentTest {

    @InjectMocks
    private OrderPayment orderPayment;

    @Mock
    private OrderService orderService;

    @Mock
    private CouponService couponService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private UserPointService userPointService;

    @Mock
    private TopProductService topProductService;

    @Test
    void 주문_결제_포인트차감_상위상품등록_완료된다(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);
        Product product = mock(Product.class);
        when(product.getProductId()).thenReturn(123L);
        Long productId = 123L; // 임의의 상품 ID

        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(1L);

        OrderItem orderItem1 = new OrderItem(order, product, 2L, 5000L); // 2개 상품
        OrderItem orderItem2 = new OrderItem(order, product, 1L, 10000L); // 1개 상품
        when(order.getOrderItems()).thenReturn(List.of(orderItem1, orderItem2));

        when(orderService.create(point, coupon)).thenReturn(order);

        Payment payment = mock(Payment.class);
        ArgumentCaptor<PaymentCreateRequest> paymentCaptor = ArgumentCaptor.forClass(PaymentCreateRequest.class);
        when(paymentService.create(paymentCaptor.capture())).thenReturn(payment);

        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(order, product, 2L, 5000L),
                new OrderItemRequest(order, product, 1L, 10000L)
        );

        PeriodType periodType = PeriodType.MONTHLY; // 예시 기간 타입

        // When
        Payment result = orderPayment.processOrderAndPayment(point, coupon, items, periodType);

        assertNotNull(result);
        verify(orderService).create(point, coupon);
        verify(orderItemService, times(2)).createOrderItem(eq(order), eq(product), anyLong(), anyLong());
        verify(paymentService).create(any(PaymentCreateRequest.class));
        verify(userPointService).use(anyLong(), anyLong());
        verify(topProductService).registerTopProduct(eq(productId), eq(periodType), eq(2L));
        verify(topProductService).registerTopProduct(eq(productId), eq(periodType), eq(1L));
        verify(orderService).completeOrder(order.getOrderId());
        verify(userCouponService).useCoupon(anyLong());  // 쿠폰 사용 처리
    }

    @Test
    void 쿠폰이_없는경우_할인없이_주문과_결제가_진행된다() {
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = null;
        Product product = mock(Product.class);
        when(product.getProductId()).thenReturn(123L);
        Long productId = 123L;

        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(1L);

        OrderItem orderItem1 = new OrderItem(order, product, 2L, 5000L); // 2개 상품, 총액 10,000
        OrderItem orderItem2 = new OrderItem(order, product, 1L, 10000L); // 1개 상품, 총액 10,000
        when(order.getOrderItems()).thenReturn(List.of(orderItem1, orderItem2));

        when(orderService.create(point, coupon)).thenReturn(order);

        Payment payment = mock(Payment.class);
        ArgumentCaptor<PaymentCreateRequest> paymentCaptor = ArgumentCaptor.forClass(PaymentCreateRequest.class);
        when(paymentService.create(paymentCaptor.capture())).thenReturn(payment);

        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(order, product, 2L, 5000L),
                new OrderItemRequest(order, product, 1L, 10000L)
        );

        PeriodType periodType = PeriodType.MONTHLY; // 예시 기간 타입

        // When
        Payment result = orderPayment.processOrderAndPayment(point, coupon, items, periodType);

        // Then
        assertNotNull(result);
        verify(orderService).create(point, coupon);
        verify(orderItemService, times(2)).createOrderItem(eq(order), eq(product), anyLong(), anyLong());
        verify(paymentService).create(any(PaymentCreateRequest.class)); // 결제 생성 확인
        verify(userPointService).use(anyLong(), anyLong()); // 포인트 차감 확인
        verify(topProductService).registerTopProduct(eq(productId), eq(periodType), eq(2L));
        verify(topProductService).registerTopProduct(eq(productId), eq(periodType), eq(1L));
        verify(orderService).completeOrder(order.getOrderId()); // 주문 완료 처리
        verify(userCouponService, never()).useCoupon(anyLong());  // 쿠폰 사용 처리 하지 않음
    }
}
