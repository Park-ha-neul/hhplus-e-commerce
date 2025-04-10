package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private Long quantity;
    private Long unitPrice;
}
