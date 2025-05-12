package kr.hhplus.be.server.domain.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;

@Getter
@NoArgsConstructor
public class PopularProduct {
    private Long productId;
    private String productName;
    private Long price;
    private Long totalCount;
    private Integer ranking;
    private PeriodType periodType;

    public PopularProduct(Long productId, String productName, Long price, Long totalCount, Integer ranking, PeriodType periodType) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.totalCount = totalCount;
        this.ranking = ranking;
        this.periodType = periodType;
    }

    public static Comparator<PopularProduct> rankAscComparator() {
        return Comparator.comparingInt(PopularProduct::getRanking);
    }

    public static Comparator<PopularProduct> rankDescComparator() {
        return Comparator.comparingInt(PopularProduct::getRanking).reversed();
    }

    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY;

        public LocalDate getStartDate(LocalDate now) {
            switch (this) {
                case DAILY:
                    return now;
                case WEEKLY:
                    return now.with(DayOfWeek.MONDAY);
                case MONTHLY:
                    return now.withDayOfMonth(1);
                default:
                    throw new IllegalArgumentException(ProductErrorCode.POPULAR_PRODUCT_PERIOD_NOT_FOUND.getMessage());
            }
        }
    }
}
