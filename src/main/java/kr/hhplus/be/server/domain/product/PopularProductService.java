package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.infrastructure.product.PopularProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularProductService {
    private final PopularProductRepository popularProductRepository;

    // todo: 인기상품 목록 조회 시 cache 적용 ttl 사용해서 (이건 periodType 마다 변경하기)
    // todo: 상품 상세 조회 시 cache 적용 한 하루마다?

    // top (limit) 뽑기
    public List<PopularProduct> getPopularProducts(PopularProduct.PeriodType periodType, boolean ascending, int limit) {
        LocalDate now = LocalDate.now();
        LocalDateTime startDate = periodType.getStartDate(now).atStartOfDay();
        LocalDateTime endDate = periodType.getEndDate(now).atTime(LocalTime.MAX);

        List<PopularProductProjection> projections = popularProductRepository
                .findByPeriodAndDateRange(startDate, endDate, limit);

        List<PopularProduct> popularProducts = projections.stream()
                .map(p -> new PopularProduct(
                        p.getProductId(),
                        p.getProductName(),
                        p.getPrice(),
                        p.getTotalCount(),
                        p.getRanking(),
                        periodType
                ))
                .collect(Collectors.toList());

        if(ascending){
            return popularProducts.stream()
                    .sorted(PopularProduct.rankAscComparator())
                    .collect(Collectors.toList());
        }else{
            return popularProducts.stream()
                    .sorted(PopularProduct.rankDescComparator())
                    .collect(Collectors.toList());
        }
    }
}
