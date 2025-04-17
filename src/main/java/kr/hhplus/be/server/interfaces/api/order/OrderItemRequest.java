package kr.hhplus.be.server.interfaces.api.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderItemRequest {
    private Long productId;
    private Long quantity;
}
