package kr.hhplus.be.server.domain.point;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.common.TransactionType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    public PointHistoryService(PointHistoryRepository pointHistoryRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
    }

    public PointHistoryEntity createHistory(Long userId, Long amount, Long balanceBefore, Long balanceAfter, String typeName){
        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.createHistory(userId, amount, balanceBefore, balanceAfter, typeName);
        return pointHistoryRepository.save(pointHistoryEntity);
    }

    public List<PointHistoryEntity> getUserPointHistory(Long userId){
        return pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
