package kr.hhplus.be.server.application.service.userPoint;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private UserPointRepository userPointRepository;

    public UserPoint getPoint(Long userId) {
        return userPointRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트 정보가 없습니다."));
    }

    @Transactional
    public void chargePoint(Long userId, long amount){
        UserPoint userPoint = userPointRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트 정보가 없습니다."));

        userPoint.chargePoint(amount);
    }

    @Transactional
    public void usePoint(Long userId, long amount){
        UserPoint userPoint = userPointRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트 정보가 없습니다."));

        userPoint.usePoint(amount);
    }
}
