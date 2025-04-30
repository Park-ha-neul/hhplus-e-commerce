package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.facade.OrderFacadeRequest;
import kr.hhplus.be.server.application.facade.OrderItemFacadeRequest;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderItemCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @Schema(description = "사용자 ID", example = "123")
    private Long userId;
    @Schema(description = "쿠폰 ID", example = "123")
    private Long userCouponId;
    @Schema(description = "주문 상품 내용", example = "[]")
    private List<OrderItemRequest> orderItems;

    public OrderCommand toCommand(){
        List<OrderItemCommand> orderItemCommands = this.orderItems.stream()
                .map(item -> new OrderItemCommand(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderCommand(
                this.getUserId(),
                this.getUserCouponId(),
                orderItemCommands
        );
    }

    public OrderFacadeRequest toRequest(){
        List<OrderItemFacadeRequest> orderItemFacadeRequests = this.orderItems.stream()
                .map(item -> new OrderItemFacadeRequest(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderFacadeRequest(
                this.getUserId(),
                this.getUserCouponId(),
                orderItemFacadeRequests
        );
    }
}
