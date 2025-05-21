package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.order.OrderItem;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private Long quantity;
    private Long unitPrice;

    public static OrderItemResponse from(OrderItem item){
        OrderItemResponse dto = new OrderItemResponse();
        dto.orderItemId = item.getOrderItemId();
        dto.productId = item.getProductId();
        dto.quantity = item.getQuantity();
        dto.unitPrice = item.getUnitPrice();
        return dto;
    }
}
