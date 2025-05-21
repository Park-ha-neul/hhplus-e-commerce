package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentPreviewResult {
    private Long orderId;
    private Long previewAmount;
}
