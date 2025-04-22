package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class PaymentTest {
    @Test
    void 결제_엔티티_생성_성공() {
        // given
        Long orderId = 1L;
        Long totalAmount = 100L;

        Payment payment = new Payment(orderId, totalAmount);

        // then
        assertNotNull(payment);
        assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getFailureReason());
    }

    @Test
    void 결제_성공_처리() {
        // given
        Long orderId = 1L;
        Long totalAmount = 100L;

        Payment payment = new Payment(orderId, totalAmount);

        // when
        payment.complete();

        // then
        assertEquals(Payment.PaymentStatus.COMPLETED, payment.getStatus());
        assertTrue(payment.isCompleted());
    }

    @Test
    void 결제_실패_처리() {
        // given
        Long orderId = 1L;
        Long totalAmount = 100L;

        Payment payment = new Payment(orderId, totalAmount);
        String reason = "잔액 부족";

        // when
        payment.fail(reason);

        // then
        assertEquals(Payment.PaymentStatus.FAIL, payment.getStatus());
        assertEquals(reason, payment.getFailureReason());
    }
}

