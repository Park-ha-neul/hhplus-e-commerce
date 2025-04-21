package kr.hhplus.be.server.domain.point;

import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository{
    PointHistory save(PointHistory pointHistory);
    Optional<PointHistory> findById(Long pointId);
    List<PointHistory> findByUserId(Long userId);
}
