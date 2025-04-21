package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public PointHistory save(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }

    @Override
    public Optional<PointHistory> findById(Long pointId) {
        return pointHistoryJpaRepository.findById(pointId);
    }

    @Override
    public List<PointHistory> findByUserId(Long userId) {
        return pointHistoryJpaRepository.findByUserId(userId);
    }
}
