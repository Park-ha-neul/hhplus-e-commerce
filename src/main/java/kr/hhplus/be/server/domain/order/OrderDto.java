package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDto {
    private Long userId;
    private List<OrderItemDto> orderItems;
}
