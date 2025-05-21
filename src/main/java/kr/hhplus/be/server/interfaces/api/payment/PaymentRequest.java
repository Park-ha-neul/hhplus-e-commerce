package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @Schema(description = "주문 ID", example = "123")
    private Long orderId;

    @Schema(description = "할인 적용한 총 금액", example = "40000")
    private Long totalAmount;

    public PaymentCommand toCommand(){
        return new PaymentCommand(
                this.orderId,
                this.totalAmount
        );
    }
}
