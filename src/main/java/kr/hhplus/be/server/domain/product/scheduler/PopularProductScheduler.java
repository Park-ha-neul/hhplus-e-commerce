package kr.hhplus.be.server.domain.product.scheduler;

import kr.hhplus.be.server.domain.product.DailyTopProduct;
import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductPersistenceService;
import kr.hhplus.be.server.domain.product.PopularProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class PopularProductScheduler {

    private final PopularProductPersistenceService persistenceService;
    private final PopularProductService popularProductService;

    @Scheduled(cron = "0 0 * * * ?")
    public void persistDailyPopularProducts() {
        List<PopularProduct> popularProducts = popularProductService.getPopularProducts(
                PopularProduct.PeriodType.DAILY,
                false,
                3
        );

        LocalDate today = LocalDate.now();

        List<DailyTopProduct> dailyTopProducts = popularProducts.stream()
                .map(p -> DailyTopProduct.builder()
                        .productId(p.getProductId())
                        .rank(p.getRanking())
                        .date(today)
                        .build())
                .collect(Collectors.toList());

        persistenceService.saveDailyTopProducts(dailyTopProducts, today);
    }
}
