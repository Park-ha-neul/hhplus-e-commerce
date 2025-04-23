package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointJpaRepository extends JpaRepository<UserPoint, Long> {
    UserPoint findByUserId(Long userId);
}
