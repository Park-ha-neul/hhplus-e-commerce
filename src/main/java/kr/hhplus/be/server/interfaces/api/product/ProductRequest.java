package kr.hhplus.be.server.interfaces.api.product;

import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private Long price;
    private Long quantity;

    public ProductRequest(String name, String description, long price, Long quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }
}
