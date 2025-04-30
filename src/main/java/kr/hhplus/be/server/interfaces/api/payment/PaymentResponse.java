package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Long totalAmount;
    private Payment.PaymentStatus status;
    private String description;

    public static PaymentResponse from(PaymentResult result){
        return PaymentResponse.builder()
                .id(result.getId())
                .orderId(result.getOrderId())
                .totalAmount(result.getTotalAmount())
                .status(result.getStatus())
                .description(result.getDescription())
                .build();
    }

    public static List<PaymentResponse> from(List<PaymentResult> resultList){
        return resultList.stream()
                .map(PaymentResponse::from)
                .collect(Collectors.toList());
    }

    public static PaymentResponse fromPayment(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .totalAmount(payment.getTotalAmount())
                .status(payment.getStatus())
                .description(payment.getFailureReason())
                .build();
    }

    public static List<PaymentResponse> fromPaymentList(List<Payment> payments){
        return payments.stream()
                .map(PaymentResponse::fromPayment)
                .collect(Collectors.toList());
    }
}
