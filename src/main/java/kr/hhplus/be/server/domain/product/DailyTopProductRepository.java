package kr.hhplus.be.server.domain.product;

import java.time.LocalDate;
import java.util.List;

public interface DailyTopProductRepository {
    List<DailyTopProduct> saveAll (List<DailyTopProduct> dailyTopProducts);
    void deleteByDate(LocalDate date);
}
