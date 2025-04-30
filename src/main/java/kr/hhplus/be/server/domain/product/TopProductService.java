package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopProductService {

    private final TopProductRepository topProductRepository;

    public List<TopProduct> getTopProductsByPeriod(TopProduct.PeriodType periodType) {
        LocalDate calculateDate = TopProduct.calculateDate(periodType);
        PageRequest topN = PageRequest.of(0, 5);
        return topProductRepository.findByPeriodTypeAndCalculateDateOrderByRankAsc(periodType, calculateDate, topN);
    }
}
