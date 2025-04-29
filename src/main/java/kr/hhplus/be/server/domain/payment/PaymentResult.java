package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {
    private Long id;
    private Long orderId;
    private Long totalAmount;
    private Payment.PaymentStatus status;
    private String description;

    public static PaymentResult of(Payment payment) {
        return new PaymentResult(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getTotalAmount(),
                payment.getStatus(),
                payment.getFailureReason()
        );
    }
}
