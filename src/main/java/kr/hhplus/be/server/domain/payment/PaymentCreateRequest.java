package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCreateRequest {
    private Long orderId;
    private Long totalAmount;
}
