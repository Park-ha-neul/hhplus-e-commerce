package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.PeriodType;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TopProductService {

    private TopProductEntityRepository topProductEntityRepository;

    public List<TopProductEntity> getTopProductsByPeriod(PeriodType periodType) {
        LocalDate calculateDate = TopProductEntity.calculateDate(periodType);
        PageRequest topN = PageRequest.of(0, 5);
        return topProductEntityRepository.findByPeriodTypeAndCalculatedDateOrderByRankAsc(periodType, calculateDate, topN);
    }
}
