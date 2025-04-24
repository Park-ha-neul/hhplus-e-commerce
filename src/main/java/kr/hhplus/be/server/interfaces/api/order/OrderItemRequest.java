package kr.hhplus.be.server.interfaces.api.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderItemRequest {
    private Long productId;
    private Long quantity;
}
