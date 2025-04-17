package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.OrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class PaymentEntityTest {
    @Test
    void 결제_엔티티_생성_성공() {
        // given
        OrderEntity dummyOrder = OrderEntity.builder().build();
        Long amount = 5000L;

        // when
        PaymentEntity payment = PaymentEntity.create(dummyOrder, amount);

        // then
        assertNotNull(payment);
        assertEquals(dummyOrder, payment.getOrderEntity());
        assertEquals(amount, payment.getTotalAmount());
        assertEquals(PaymentStatus.PENDING, payment.getType());
        assertNull(payment.getFailureReason());
    }

    @Test
    void 결제_성공_처리() {
        // given
        PaymentEntity payment = PaymentEntity.create(OrderEntity.builder().build(), 10000L);

        // when
        payment.complete();

        // then
        assertEquals(PaymentStatus.COMPLETED, payment.getType());
        assertTrue(payment.isCompleted());
    }

    @Test
    void 결제_실패_처리() {
        // given
        PaymentEntity payment = PaymentEntity.create(OrderEntity.builder().build(), 10000L);
        String reason = "잔액 부족";

        // when
        payment.fail(reason);

        // then
        assertEquals(PaymentStatus.FAIL, payment.getType());
        assertEquals(reason, payment.getFailureReason());
        assertFalse(payment.isCompleted());
    }
}

