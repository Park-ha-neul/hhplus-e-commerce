package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.ProductCommand;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @Schema(description = "상품 명", example = "상품 A")
    private String name;
    @Schema(description = "상품 설명", example = "상품 A에 대한 설명입니다.")
    private String description;
    @Schema(description = "상품 가격", example = "1000")
    private Long price;
    @Schema(description = "상품 수량", example = "2000")
    private Long quantity;

    public ProductCommand toCommand(){
        return new ProductCommand(
                this.name,
                this.description,
                this.price,
                this.quantity
        );
    }
}
