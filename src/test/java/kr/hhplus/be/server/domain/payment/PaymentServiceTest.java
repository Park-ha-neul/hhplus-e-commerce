package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderEntityRepository;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.interfaces.api.payment.PaymentCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private PaymentEntityRepository paymentEntityRepository;

    @Mock
    private OrderEntityRepository orderEntityRepository;

    @Test
    void 결제_생성_성공() {
        // given
        Long orderId = 1L;
        Long totalPrice = 5000L;

        OrderItemEntity item1 = OrderItemEntity.builder().quantity(1L).unitPrice(3000L).build();
        OrderItemEntity item2 = OrderItemEntity.builder().quantity(1L).unitPrice(2000L).build();
        List<OrderItemEntity> items = List.of(item1, item2);

        OrderEntity order = OrderEntity.builder()
                .orderId(orderId)
                .orderItemEntities(new ArrayList<>())
                .build();
        order.getOrderItemEntities().addAll(items);

        when(orderEntityRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCreateRequest request = new PaymentCreateRequest(orderId);

        // when
        PaymentEntity result = paymentService.create(request);

        // then
        assertNotNull(result);
        assertEquals(order, result.getOrderEntity());
        assertEquals(totalPrice, result.getTotalAmount());
        assertEquals(PaymentStatus.PENDING, result.getType());
        verify(paymentEntityRepository).save(any(PaymentEntity.class));
    }

    @Test
    void 결제_조회_성공() {
        // given
        Long paymentId = 1L;
        PaymentEntity payment = mock(PaymentEntity.class);
        when(paymentEntityRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        PaymentEntity result = paymentService.getPayment(paymentId);

        // then
        assertEquals(payment, result);
    }

    @Test
    void 결제_조회_실패() {
        // given
        Long paymentId = 99L;
        when(paymentEntityRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> paymentService.getPayment(paymentId));
    }

    @Test
    void 결제_성공_처리() {
        // given
        Long paymentId = 1L;
        PaymentEntity payment = PaymentEntity.create(mock(OrderEntity.class), 10000L);
        when(paymentEntityRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        paymentService.updateStatusComplete(paymentId);

        // then
        assertEquals(PaymentStatus.COMPLETED, payment.getType());
        verify(paymentEntityRepository).save(payment);
    }

    @Test
    void 결제_실패_처리() {
        // given
        Long paymentId = 2L;
        String reason = "카드 한도 초과";
        PaymentEntity payment = PaymentEntity.create(mock(OrderEntity.class), 15000L);
        when(paymentEntityRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        // when
        paymentService.updateStatusFail(paymentId, reason);

        // then
        assertEquals(PaymentStatus.FAIL, payment.getType());
        assertEquals(reason, payment.getFailureReason());
        verify(paymentEntityRepository).save(payment);
    }

    @Test
    void 상태별_결제_조회() {
        // given
        PaymentStatus status = PaymentStatus.COMPLETED;
        List<PaymentEntity> list = List.of(mock(PaymentEntity.class));
        when(paymentEntityRepository.findAllByStatus(status)).thenReturn(list);

        // when
        List<PaymentEntity> result = paymentService.getPayments(status);

        // then
        assertEquals(1, result.size());
        assertEquals(list, result);
    }

    @Test
    void 유저별_결제_조회() {
        // given
        Long userId = 123L;
        List<PaymentEntity> list = List.of(mock(PaymentEntity.class));
        when(paymentEntityRepository.findByUserId(userId)).thenReturn(list);

        // when
        List<PaymentEntity> result = paymentService.getPaymentByUserId(userId);

        // then
        assertEquals(1, result.size());
        assertEquals(list, result);
    }
}
