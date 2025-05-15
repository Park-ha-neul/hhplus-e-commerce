package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.DailyTopProduct;
import kr.hhplus.be.server.domain.product.DailyTopProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyTopProductRepositoryImpl implements DailyTopProductRepository {

    private final DailyTopProductJpaRepository dailyTopProductJpaRepository;

    @Override
    public List<DailyTopProduct> saveAll(List<DailyTopProduct> dailyTopProducts) {
        return dailyTopProductJpaRepository.saveAll(dailyTopProducts);
    }

    @Override
    public void deleteByDate(LocalDate date) {
        dailyTopProductJpaRepository.deleteByDate(date);
    }
}
