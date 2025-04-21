package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public void charge(Long userId, Long amount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        Long balanceBefore = userPoint.getPoint();
        userPoint.charge(amount);
        Long balanceAfter = userPoint.getPoint();
        PointHistory pointHistory = PointHistory.chargeHistory(userId, amount, balanceBefore, balanceAfter);
        pointHistoryRepository.save(pointHistory);
        userPointRepository.save(userPoint);
    }

    public void use(Long userId, Long amount) {
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        Long balanceBefore = userPoint.getPoint();
        userPoint.use(amount);
        Long balanceAfter = userPoint.getPoint();
        PointHistory pointHistory = PointHistory.useHistory(userId, amount, balanceBefore, balanceAfter);
        pointHistoryRepository.save(pointHistory);
        userPointRepository.save(userPoint);
    }

    public List<PointHistory> getUserPointHistory(Long userId){
        return pointHistoryRepository.findByUserId(userId);
    }
}
