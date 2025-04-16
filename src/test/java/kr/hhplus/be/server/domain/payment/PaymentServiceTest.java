package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void 결제_조회(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);

        Payment payment = Payment.create(request);
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPayment(1L);

        assertEquals(payment, result);
    }

    @Test
    void 결제_조회시_데이터_없는경우_예외처리(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.getPayment(payment.getPaymentId());
        });

        assertEquals(ErrorCode.PAYMENT_NOT_FOUND.getMessage(), e.getMessage());

    }

    @Test
    void 결제_성공_업데이트(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));
        paymentService.updateStatusComplete(payment.getPaymentId());

        assertEquals(PaymentType.COMPLETED, payment.getType());
    }

    @Test
    void 결제_실패_업데이트(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        when(paymentRepository.findById(payment.getPaymentId())).thenReturn(Optional.of(payment));
        paymentService.updateStatusFail(payment.getPaymentId(), "실패다");

        assertEquals(PaymentType.FAIL, payment.getType());
        assertEquals("실패다", payment.getFailureReason());

    }
}
