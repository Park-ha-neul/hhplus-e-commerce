package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private List<OrderItemResponse> items;
    private Long userCouponId;
    private Order.OrderStatus status;

    public static OrderResponse from(OrderResult result) {
        return OrderResponse.builder()
                .id(result.getId())
                .userId(result.getUserId())
                .items(result.getItem().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList())
                )
                .userCouponId(result.getUserCouponId())
                .status(result.getStatus())
                .build();
    }

    public static List<OrderResponse> from(List<OrderResult> resultList){
        return resultList.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public static OrderResponse fromOrder(Order order){
        return OrderResponse.builder()
                .id(order.getOrderId())
                .userId(order.getOrderId())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList())
                )
                .userCouponId(order.getCouponId())
                .status(order.getStatus())
                .build();
    };

    public static List<OrderResponse> fromOrderList(Page<Order> orderList){
        return orderList.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }
}
