package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {
    @Test
    void 결제_상태_pending_변경(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);

        assertEquals(PaymentType.PENDING, payment.getType());
    }

    @Test
    void 결제_상태_completed_변경(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        payment.complete();

        assertEquals(PaymentType.COMPLETED, payment.getType());
    }

    @Test
    void 결제_취소(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        payment.fail("실패했음");

        assertEquals(PaymentType.FAIL, payment.getType());
        assertEquals("실패했음", payment.getFailureReason());
    }

    @Test
    void 결제_상태_완료_여부_확인(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);
        payment.complete();

        assertTrue(payment.isCompleted());
    }

    @Test
    void 결제_생성(){
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        Payment payment = Payment.create(request);

        assertNotNull(payment);
    }
}

