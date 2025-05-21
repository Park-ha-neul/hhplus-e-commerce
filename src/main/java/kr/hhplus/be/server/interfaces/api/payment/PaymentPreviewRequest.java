package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.payment.PaymentPreviewCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPreviewRequest {
    @Schema(description = "주문 ID", example = "123")
    private Long orderId;

    public PaymentPreviewCommand toCommand(){
        return new PaymentPreviewCommand(
                this.orderId
        );
    }
}
