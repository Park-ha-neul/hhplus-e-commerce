package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.PeriodType;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "top_product")
public class TopProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topProductId;

    private ProductEntity productEntity;

    private int rank;
    private Long totalCount;
    private LocalDate calculateDate;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    public static LocalDate calculateDate(PeriodType periodType){
        switch (periodType){
            case DAILY :
                return LocalDate.now();
            case WEEKLY:
                return LocalDate.now().with(DayOfWeek.MONDAY);
            case MONTHLY:
                return LocalDate.now().withDayOfMonth(1);
            default:
                throw new IllegalArgumentException(ProductErrorCode.TOP_PRODUCT_PERIOD_NOT_FOUND.getMessage());
        }
    }

    private TopProductEntity(ProductEntity productEntity, PeriodType periodType, LocalDate calculateDate, Long totalCount) {
        this.productEntity = productEntity;
        this.periodType = periodType;
        this.calculateDate = calculateDate;
        this.totalCount = totalCount;
    }

    public static TopProductEntity create(ProductEntity productEntity, PeriodType periodType, LocalDate calculateDate, Long count) {
        return new TopProductEntity(productEntity, periodType, calculateDate, count);
    }

    public void addCount(Long count) {
        if (this.totalCount == null) {
            this.totalCount = 0L;
        }
        this.totalCount += count;
    }
}
