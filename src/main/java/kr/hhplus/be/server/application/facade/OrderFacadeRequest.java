package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.OrderCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OrderFacadeRequest {
    private Long userId;
    private Long userCouponId;
    private List<OrderItemFacadeRequest> orderItems;

    public OrderCommand toCommand(){
        return new OrderCommand(
                this.userId,
                this.userCouponId,
                this.orderItems.stream()
                        .map(OrderItemFacadeRequest::toCommand)
                        .collect(Collectors.toList())
        );
    }
}
