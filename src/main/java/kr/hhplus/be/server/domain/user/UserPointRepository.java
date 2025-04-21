package kr.hhplus.be.server.domain.user;

public interface UserPointRepository {
    UserPoint save (UserPoint userPoint);
    UserPoint findByUserId(Long userId);
}
