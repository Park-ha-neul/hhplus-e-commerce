package kr.hhplus.be.server.application.payment.usecase;

import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompletePaymentTest {

    @InjectMocks
    private CompletePayment completePayment;

    @Mock
    private UserPointService userPointService;

    @Mock
    private PaymentService paymentService;

    @Test
    void 결제_완료후_포인트_차감확인(){
        Long userId = 1L;
        Long paymentId = 1001L;
        Long pointToUse = 500L;

        // When
        completePayment.execute(userId, paymentId, pointToUse);

        // Then
        verify(userPointService, times(1)).use(userId, pointToUse);
        verify(paymentService, times(1)).updateStatusComplete(paymentId);
    }
}
