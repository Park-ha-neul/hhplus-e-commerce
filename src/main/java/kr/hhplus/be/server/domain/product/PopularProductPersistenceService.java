package kr.hhplus.be.server.domain.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularProductPersistenceService {

    private final DailyTopProductRepository dailyTopProductRepository;

    @Transactional
    public void saveDailyTopProducts(List<DailyTopProduct> products, LocalDate date){
        dailyTopProductRepository.deleteByDate(date);
        dailyTopProductRepository.saveAll(products);
    }
}
