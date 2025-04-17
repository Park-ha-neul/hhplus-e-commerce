package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.point.PointHistoryEntity;
import kr.hhplus.be.server.domain.point.PointHistoryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointEntityRepository userPointEntityRepository;
    private final PointHistoryEntityRepository pointHistoryEntityRepository;

    public void charge(Long userId, Long amount) {
        UserPointEntity entity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        Long balanceBefore = entity.getPoint().getPoint();
        entity.getPoint().charge(amount);
        Long balanceAfter = entity.getPoint().getPoint();
        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.chargeHistory(entity, amount, balanceBefore, balanceAfter);
        pointHistoryEntityRepository.save(pointHistoryEntity);
        userPointEntityRepository.save(entity);
    }

    public void use(Long userId, Long amount) {
        UserPointEntity entity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        Long balanceBefore = entity.getPoint().getPoint();
        entity.getPoint().use(amount);
        Long balanceAfter = entity.getPoint().getPoint();
        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.useHistory(entity, amount, balanceBefore, balanceAfter);
        pointHistoryEntityRepository.save(pointHistoryEntity);
        userPointEntityRepository.save(entity);
    }

    public List<PointHistoryEntity> getUserPointHistory(Long userId){
        UserPointEntity entity = userPointEntityRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        return pointHistoryEntityRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
