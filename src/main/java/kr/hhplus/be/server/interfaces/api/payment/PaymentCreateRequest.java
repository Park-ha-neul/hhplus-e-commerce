package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {
    @Schema(description = "주문 ID", example = "123")
    private Long orderId;
}
