package kr.hhplus.be.server.domain.product;

import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;

@Getter
public class PopularProduct {
    private final Long productId;
    private final String productName;
    private final Long price;
    private final Long totalCount;
    private final Integer ranking;
    private final PeriodType periodType;

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

        public LocalDate getEndDate(LocalDate now) {
            switch (this) {
                case DAILY:
                    return now;
                case WEEKLY:
                    return now.with(DayOfWeek.SUNDAY);
                case MONTHLY:
                    return now.withDayOfMonth(now.lengthOfMonth());
                default:
                    throw new IllegalArgumentException(ProductErrorCode.POPULAR_PRODUCT_PERIOD_NOT_FOUND.getMessage());
            }
        }
    }
}
