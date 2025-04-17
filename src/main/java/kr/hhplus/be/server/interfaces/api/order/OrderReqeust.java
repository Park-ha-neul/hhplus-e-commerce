package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderReqeust {
    @Schema(description = "사용자 ID", example = "123")
    private Long userId;
    @Schema(description = "쿠폰 ID", example = "123")
    private Long couponId;
    @Schema(description = "주문 상품 내용", example = "[]")
    private List<OrderItemRequest> orderItems;
}
