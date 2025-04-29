package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void 결제_생성_성공() {
        // given
        Long userId = 1L;
        Long couponId = 2L;
        Long orderId = 1L;
        Long totalPrice = 5000L;

        OrderItem item1 = OrderItem.builder().quantity(1L).price(3000L).build();
        OrderItem item2 = OrderItem.builder().quantity(1L).price(2000L).build();
        List<OrderItem> items = List.of(item1, item2);

        Order order = Order.builder()
                .userId(userId)
                .couponId(couponId)
                .build();
        order.getItems().addAll(items);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Payment payment = new Payment(order.getOrderId(), 5000L);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentCommand command = new PaymentCommand(orderId);

        // when
        PaymentResult result = paymentService.create(command);

        // then
        assertNotNull(result);
        assertEquals(totalPrice, result.getTotalAmount());
        assertEquals(Payment.PaymentStatus.PENDING, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 결제_조회_성공() {
        // given
        Long paymentId = 1L;
        Payment payment = mock(Payment.class);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        Payment result = paymentService.getPayment(paymentId);

        // then
        assertEquals(payment, result);
    }

    @Test
    void 결제_조회_실패() {
        // given
        Long paymentId = 99L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> paymentService.getPayment(paymentId));
    }

    @Test
    void 결제_성공_처리() {
        // given
        Long paymentId = 1L;
        Long orderId = 1L;
        Long totalAmount = 2000L;
        Payment payment = new Payment(orderId, totalAmount);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        paymentService.updateStatusComplete(paymentId);

        // then
        assertEquals(Payment.PaymentStatus.COMPLETED, payment.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    void 결제_실패_처리() {
        // given
        Long paymentId = 2L;
        String reason = "카드 한도 초과";
        Long orderId = 1L;
        Long totalAmount = 2000L;
        Payment payment = new Payment(orderId, totalAmount);
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        paymentService.updateStatusFail(paymentId, reason);

        // then
        assertEquals(Payment.PaymentStatus.FAIL, payment.getStatus());
        assertEquals(reason, payment.getFailureReason());
        verify(paymentRepository).save(payment);
    }

    @Test
    void 상태별_결제_조회() {
        // given
        Payment.PaymentStatus status = Payment.PaymentStatus.COMPLETED;
        List<Payment> list = List.of(mock(Payment.class));
        when(paymentRepository.findAllByStatus(status)).thenReturn(list);

        // when
        List<Payment> result = paymentService.getPayments(status);

        // then
        assertEquals(1, result.size());
        assertEquals(list, result);
    }

    @Test
    void 유저별_결제_조회() {
        // given
        Long userId = 123L;
        Long orderId = 1L;
        Payment payment = mock(Payment.class);
        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(orderId);
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);

        // when
        List<Payment> result = paymentService.getUserPayments(userId, null);

        // then
        assertEquals(1, result.size());
        assertEquals(payment, result.get(0));
    }
}
