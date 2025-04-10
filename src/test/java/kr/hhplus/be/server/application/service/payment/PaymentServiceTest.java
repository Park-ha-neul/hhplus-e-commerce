package kr.hhplus.be.server.application.service.payment;

import kr.hhplus.be.server.application.facade.UserPointFacadeService;
import kr.hhplus.be.server.application.service.order.OrderService;
import kr.hhplus.be.server.application.service.product.TopProductService;
import kr.hhplus.be.server.domain.common.PaymentType;
import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCreateRequest;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private UserPointFacadeService userPointFacadeService;

    @Mock
    private TopProductService topProductService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    public void 결제_등록_성공(){
        Long orderId = 1L;
        Long totalAmount = 5000L;
        PaymentCreateRequest request = new PaymentCreateRequest(orderId, totalAmount);

        Order mockOrder = mock(Order.class); // 필요한 필드 설정 가능
        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);

        Payment savedPayment = new Payment();
        savedPayment.setOrder(mockOrder);
        savedPayment.setTotalAmount(totalAmount);
        savedPayment.setPending();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // when
        Payment result = paymentService.createPayment(request);

        assertNotNull(result);
        assertEquals(mockOrder, result.getOrder());
        assertEquals(totalAmount, result.getTotalAmount());
        assertEquals(PaymentType.PENDING, result.getType());

        verify(orderService).getOrderById(orderId);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    public void 결제_진행중_결제가_없는경우_예외처리(){
        Long paymentId = 99L;

        when(paymentRepository.findByOrderId(paymentId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(paymentId);
        });

        assertEquals("결제를 찾을 수가 없습니다.", e.getMessage());
    }

    @Test
    public void 이미_완료된_결제_예외처리(){
        Order order = mock(Order.class);
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTotalAmount(5000L);
        payment.complete();

        when(paymentRepository.findByOrderId(payment.getPaymentId())).thenReturn(Optional.of(payment));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(payment.getPaymentId());
        });

        assertEquals("이미 완료된 결제입니다.", e.getMessage());
    }

    @Test
    public void 결제_실패_잔액_부족(){
        // given
        Long paymentId = 1L;
        Long totalAmount = 10000L;
        Long userId = 42L;

        Order mockOrder = mock(Order.class);
        UserPoint mockUserPoint = mock(UserPoint.class);

        when(mockOrder.getUserPoint()).thenReturn(mockUserPoint);
        when(mockUserPoint.getUserId()).thenReturn(userId);

        Payment payment = new Payment();
        payment.setOrder(mockOrder);
        payment.setTotalAmount(totalAmount);
        payment.setPending();

        when(paymentRepository.findByOrderId(paymentId)).thenReturn(Optional.of(payment));

        doThrow(new IllegalArgumentException("포인트가 부족합니다."))
                .when(userPointFacadeService).usePoint(userId, totalAmount);

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(paymentId);
        });

        // then
        assertEquals("포인트가 부족합니다.", e.getMessage());
    }

    @Test
    public void 결제_완료(){
        // given
        Long paymentId = 1L;
        Long totalAmount = 5000L;
        Long userId = 42L;
        Long productId = 99L;
        Long quantity = 3L;

        Order mockOrder = mock(Order.class);
        UserPoint mockUserPoint = mock(UserPoint.class);

        when(mockOrder.getUserPoint()).thenReturn(mockUserPoint);
        when(mockUserPoint.getUserId()).thenReturn(userId);

        Payment payment = new Payment();
        payment.setOrder(mockOrder);
        payment.setTotalAmount(totalAmount);
        payment.setPending();

        when(paymentRepository.findByOrderId(paymentId)).thenReturn(Optional.of(payment));

        // 포인트 사용은 정상 호출 (예외 없음)
        doNothing().when(userPointFacadeService).usePoint(userId, totalAmount);

        // when
        Payment result = paymentService.processPayment(paymentId);

        // then
        assertNotNull(result);
        assertTrue(result.isCompleted());
        assertEquals(PaymentType.COMPLETED, result.getType());

        verify(paymentRepository).findByOrderId(paymentId);
        verify(userPointFacadeService).usePoint(userId, totalAmount);

        for (PeriodType period : PeriodType.values()) {
            LocalDate date = TopProduct.calculateCalculatedDate(period);
            verify(topProductService).saveOrUpdateTopProduct(productId, quantity, date, period);
        }
    }
}
