package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TopProduct {
    @Id
    private Long topProductId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int rank;
    private Long totalCount;
    private LocalDate calculateDate; // 통계 계산 기준일

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    public static LocalDate calculateCalculatedDate(PeriodType periodType){
        switch (periodType){
            case DAILY :
                return LocalDate.now();
            case WEEKLY:
                return LocalDate.now().with(DayOfWeek.MONDAY); // 이번 주 월요일
            case MONTHLY:
                return LocalDate.now().withDayOfMonth(1); // 이번 달 1일
            default:
                throw new IllegalArgumentException("정의되지 않은 날짜 기준입니다.");
        }
    }

    private TopProduct(Product product, PeriodType periodType, LocalDate calculateDate, Long totalCount) {
        this.product = product;
        this.periodType = periodType;
        this.calculateDate = calculateDate;
        this.totalCount = totalCount;
    }

    public static TopProduct create(Product product, PeriodType periodType, LocalDate calculateDate, Long count) {
        return new TopProduct(product, periodType, calculateDate, count);
    }

    public void addCount(Long count) {
        if (this.totalCount == null) {
            this.totalCount = 0L;
        }
        this.totalCount += count;
    }
}
