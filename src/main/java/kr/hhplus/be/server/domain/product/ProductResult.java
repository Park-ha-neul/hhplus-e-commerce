package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ProductResult {
    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long quantity;
    private Product.ProductStatus status;

    public static ProductResult of(Product product){
        return new ProductResult(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getStatus()
        );
    }

    public static List<ProductResult> from(List<Product> productList){
        return productList.stream()
                .map(ProductResult::of)
                .collect(Collectors.toList());
    }
}
