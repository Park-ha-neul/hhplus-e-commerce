package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.OrderItemCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemFacadeRequest {
    private Long productId;
    private Long quantity;

    public OrderItemCommand toCommand(){
        return new OrderItemCommand(
                this.productId,
                this.quantity
        );
    }
}
