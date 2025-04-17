package kr.hhplus.be.server.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryEntityRepository extends JpaRepository<PointHistoryEntity, Long> {
    List<PointHistoryEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
