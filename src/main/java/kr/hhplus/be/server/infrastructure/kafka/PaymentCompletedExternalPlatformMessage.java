package kr.hhplus.be.server.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PaymentCompletedExternalPlatformMessage {
    private final Long orderId;
    private final Long userId;
    private final List<OrderItemDto> orderItems;
    private final LocalDateTime updatedDate;

    @JsonCreator
    public PaymentCompletedExternalPlatformMessage(
            @JsonProperty("orderId") Long orderId,
            @JsonProperty("userId") Long userId,
            @JsonProperty("orderItems") List<OrderItemDto> orderItems,
            @JsonProperty("updatedDate") LocalDateTime updatedDate
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderItems = orderItems;
        this.updatedDate = updatedDate;
    }
}
