package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCreateRequest {
    private Order order;
    private Long totalAmount;
}
