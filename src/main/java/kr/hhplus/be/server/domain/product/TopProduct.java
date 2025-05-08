package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "top_product", indexes = {
        @Index(name = "idx_period_calculate_rank", columnList = "period_type, calculate_date, ranking")
})
public class TopProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topProductId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "ranking")
    private int rank;

    @Column(name = "total_count")
    private Long totalCount;

    @Column(name = "calculate_date")
    private LocalDate calculateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type")
    private PeriodType periodType;

    @Setter
    @Transient
    private Product product;

    public enum PeriodType{
        DAILY, WEEKLY, MONTHLY
    }

    public TopProduct(){

    }

    public TopProduct(Long productId, int rank, Long totalCount, LocalDate calculateDate, PeriodType periodType) {
        this.productId = productId;
        this.rank = rank;
        this.totalCount = totalCount;
        this.calculateDate = calculateDate;
        this.periodType = periodType;
    }

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

    public void addCount(Long count) {
        if (this.totalCount == null) {
            this.totalCount = 0L;
        }
        this.totalCount += count;
    }
}
