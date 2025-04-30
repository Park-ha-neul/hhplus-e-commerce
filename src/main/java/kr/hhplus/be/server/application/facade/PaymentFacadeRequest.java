package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.payment.PaymentCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentFacadeRequest {
    private Long orderId;

    public PaymentCommand toCommand(){
        return new PaymentCommand(
                this.orderId
        );
    }
}
