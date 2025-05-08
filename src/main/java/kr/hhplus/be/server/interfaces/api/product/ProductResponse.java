package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductResult;
import kr.hhplus.be.server.domain.product.TopProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Long quantity;
    private Product.ProductStatus status;
    private Long price;

    public static ProductResponse from(ProductResult result) {
        return ProductResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .description(result.getDescription())
                .quantity(result.getQuantity())
                .status(result.getStatus())
                .price(result.getPrice())
                .build();
    }

    public static List<ProductResponse> from(List<ProductResult> productResults){
        return productResults.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public static ProductResponse fromProdcut(Product product){
        return ProductResponse.builder()
                .id(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .status(product.getStatus())
                .price(product.getPrice())
                .build();
    }

    public static List<ProductResponse> fromProducts(List<Product> products){
        return products.stream()
                .map(ProductResponse::fromProdcut)
                .collect(Collectors.toList());
    }
}
