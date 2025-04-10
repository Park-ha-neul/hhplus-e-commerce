package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.common.PaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {
    @Test
    public void 결제_상태_pending_변경(){
        Payment payment = new Payment();
        payment.setPending();

        assertEquals(PaymentType.PENDING, payment.getType());
    }

    @Test
    public void 결제_상태_completed_변경(){
        Payment payment = new Payment();
        payment.complete();

        assertEquals(PaymentType.COMPLETED, payment.getType());
    }

    @Test
    public void 결제_상태_완료_여부_확인(){
        Payment payment = new Payment();
        payment.complete();

        assertTrue(payment.isCompleted());
    }
}

