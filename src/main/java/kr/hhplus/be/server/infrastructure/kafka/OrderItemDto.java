package kr.hhplus.be.server.infrastructure.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderItemDto {
    private final Long productId;
    private final Long quantity;
    private final Long unitPrice;

    @JsonCreator
    public OrderItemDto(
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") Long quantity,
            @JsonProperty("unitPrice") Long unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
