package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentPreviewCommand {
    private Long orderId;
}
