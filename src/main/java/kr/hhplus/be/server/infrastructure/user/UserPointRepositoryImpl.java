package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointJpaRepository userPointJpaRepository;

    @Override
    public UserPoint save(UserPoint userPoint) {
        return userPointJpaRepository.save(userPoint);
    }

    @Override
    public UserPoint findByUserId(Long userId) {
        return userPointJpaRepository.findByUserId(userId);
    }
}
