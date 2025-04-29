package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentFacadeResponse {
    private Long id;
    private Long orderId;
    private Long totalAmount;
    private Payment.PaymentStatus status;
    private String description;

    public static PaymentFacadeResponse of(Payment payment){
        return new PaymentFacadeResponse(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getTotalAmount(),
                payment.getStatus(),
                payment.getFailureReason()
        );
    }
}
