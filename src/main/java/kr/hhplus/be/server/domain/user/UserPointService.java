package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Service;

@Service
public class UserPointService {

    private final UserPointEntityRepository repository;

    public UserPointService(UserPointEntityRepository repository){
        this.repository = repository;
    }

    public UserPoint getUserPoint(Long userId) {
        UserPointEntity entity = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 포인트 정보가 없습니다."));

        return entity.getPoint();
    }

    public void charge(Long userId, Long amount) {
        UserPointEntity entity = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 포인트 정보 없음"));

        entity.getPoint().charge(amount);
        repository.save(entity);
    }

    public void use(Long userId, Long amount) {
        UserPointEntity entity = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 포인트 정보 없음"));

        entity.getPoint().use(amount);
        repository.save(entity);
    }
}
