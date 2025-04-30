package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductCommand {
    private String name;
    private String description;
    private Long price;
    private Long quantity;
}
