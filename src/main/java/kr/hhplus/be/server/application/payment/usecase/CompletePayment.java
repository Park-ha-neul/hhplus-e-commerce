package kr.hhplus.be.server.application.payment.usecase;

import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserPointService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompletePayment {

    private final UserPointService userPointService;
    private final PaymentService paymentService;

    public void execute(Long userId, Long paymentId, Long pointToUse) {
        userPointService.use(userId, pointToUse);
        paymentService.updateStatusComplete(paymentId);
    }
}
