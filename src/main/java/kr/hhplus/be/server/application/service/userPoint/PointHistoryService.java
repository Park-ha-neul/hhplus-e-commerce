package kr.hhplus.be.server.application.service.userPoint;

import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointHistoryService {

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    public List<PointHistory> getHistories(Long userId){
        return pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public PointHistory createHistories(Long userId, Long amount, Long balanceBefore, Long balanceAfter, TransactionType type){
        if (userId == null || amount == null || balanceBefore == null || balanceAfter == null || type == null) {
            throw new IllegalArgumentException("모든 필드를 입력해야 합니다.");
        }
        PointHistory history = PointHistory.of(userId, amount, balanceBefore, balanceAfter, type);
        return pointHistoryRepository.save(history);
    }
}
