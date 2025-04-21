package kr.hhplus.be.server.domain.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TopProductService {

    private TopProductRepository topProductRepository;

    public List<TopProduct> getTopProductsByPeriod(TopProduct.PeriodType periodType) {
        LocalDate calculateDate = TopProduct.calculateDate(periodType);
        PageRequest topN = PageRequest.of(0, 5);
        return topProductRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculateDate, topN);
    }
}
