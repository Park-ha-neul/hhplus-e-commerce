package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderCommand {
        private Long userId;
        private Long userCouponId;
        private List<OrderItemCommand> orderItems;
}
