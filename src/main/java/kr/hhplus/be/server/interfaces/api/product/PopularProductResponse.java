package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.PopularProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class PopularProductResponse {
    private Long productId;
    private String productName;
    private Long price;
    private Long totalCount;
    private int ranking;

    public static PopularProductResponse fromPopularProduct(PopularProduct popularProduct){
        return PopularProductResponse.builder()
                .productId(popularProduct.getProductId())
                .productName(popularProduct.getProductName())
                .price(popularProduct.getPrice())
                .totalCount(popularProduct.getTotalCount())
                .ranking(popularProduct.getRanking())
                .build();
    }

    public static List<PopularProductResponse> fromPopularProducts(List<PopularProduct> popularProducts){
        return popularProducts.stream()
                .map(PopularProductResponse::fromPopularProduct)
                .collect(Collectors.toList());
    }
}
