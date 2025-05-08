package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class TopProductResponse {
    private Long id;
    private Long productId;
    private int rank;
    private Long totalCount;
    private LocalDate calculateDate;
    private TopProduct.PeriodType periodType;
    private Product product;

    public static TopProductResponse fromTopProduct(TopProduct topProduct){
        return TopProductResponse.builder()
                .id(topProduct.getTopProductId())
                .productId(topProduct.getProductId())
                .rank(topProduct.getRank())
                .totalCount(topProduct.getTotalCount())
                .calculateDate(topProduct.getCalculateDate())
                .periodType(topProduct.getPeriodType())
                .product(topProduct.getProduct())
                .build();
    }

    public static List<TopProductResponse> fromTopProducts(List<TopProduct> topProducts){
        return topProducts.stream()
                .map(TopProductResponse::fromTopProduct)
                .collect(Collectors.toList());
    }
}
