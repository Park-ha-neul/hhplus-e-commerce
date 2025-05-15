package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "daily_top_product")
public class DailyTopProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer rank;

    private Integer count;

    private LocalDate date;
}
