package kr.hhplus.be.server.domain.product;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PopularProductMapper {

    private final ProductService productService;

    public PopularProductMapper(ProductService productService) {
        this.productService = productService;
    }

    public List<PopularProduct> fromRedisTuples(Set<ZSetOperations.TypedTuple<Object>> tuples, boolean ascending) {
        List<PopularProduct> products = tuples.stream()
                .map(tuple -> {
                    String productIdStr;
                    Object value = tuple.getValue();

                    if (value instanceof String) {
                        productIdStr = (String) value;
                    } else if (value instanceof LinkedHashMap) {
                        LinkedHashMap map = (LinkedHashMap) value;
                        productIdStr = String.valueOf(map.get("productId"));
                    } else {
                        throw new IllegalStateException("알 수 없는 타입: " + value.getClass());
                    }
                    Double score = tuple.getScore();

                    Product product = productService.getProduct(Long.parseLong(productIdStr));
                    return new PopularProduct(
                            product.getProductId(),
                            product.getName(),
                            product.getPrice(),
                            score != null ? score.longValue() : 0L,
                            0,
                            null
                    );
                })
                .collect(Collectors.toList());

        Comparator<PopularProduct> comparator = ascending
                ? PopularProduct.rankAscComparator()
                : PopularProduct.rankDescComparator();

        return products.stream().sorted(comparator).collect(Collectors.toList());
    }
}
