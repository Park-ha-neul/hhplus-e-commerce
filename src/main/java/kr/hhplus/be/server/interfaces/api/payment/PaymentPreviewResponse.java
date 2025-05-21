package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.domain.payment.PaymentPreviewResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PaymentPreviewResponse {
    private Long orderId;
    private Long previewAmount;

    public static PaymentPreviewResponse from(PaymentPreviewResult result){
        return PaymentPreviewResponse.builder()
                .orderId(result.getOrderId())
                .previewAmount(result.getPreviewAmount())
                .build();
    }
}
