package kr.hhplus.be.server.domain.user;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.PointHistoryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void charge(Long userId, Long amount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        Long balanceBefore = userPoint.getPoint();
        userPoint.charge(amount);
        Long balanceAfter = userPoint.getPoint();
        PointHistory pointHistory = PointHistory.chargeHistory(userId, amount, balanceBefore, balanceAfter);
        pointHistoryRepository.save(pointHistory);
        userPointRepository.save(userPoint);
    }

    @Transactional
    public void use(Long userId, Long amount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        Long balanceBefore = userPoint.getPoint();
        userPoint.use(amount);
        Long balanceAfter = userPoint.getPoint();
        PointHistory pointHistory = PointHistory.useHistory(userId, amount, balanceBefore, balanceAfter);
        pointHistoryRepository.save(pointHistory);
        userPointRepository.save(userPoint);
    }

    @Transactional
    public List<PointHistoryResult> getUserPointHistory(Long userId){
        List<PointHistory> pointHistories = pointHistoryRepository.findByUserId(userId);
        return pointHistories.stream()
                .map(PointHistoryResult::of)
                .collect(Collectors.toList());
    }
}
