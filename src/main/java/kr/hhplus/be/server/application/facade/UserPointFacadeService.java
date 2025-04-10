package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.service.userPoint.PointHistoryService;
import kr.hhplus.be.server.application.service.userPoint.PointService;
import kr.hhplus.be.server.application.service.userPoint.UserService;
import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPointFacadeService {

    @Autowired
    private UserService userService;

    @Autowired
    private PointService pointService;

    @Autowired
    private PointHistoryService pointHistoryService;

    @Transactional
    public void usePoint(Long userId, Long amount){
        // 1. 포인트 정보 가져오기
        UserPoint point = pointService.getPoint(userId);
        // 2. 포인트 차감
        Long balanceBefore = point.getPoint();
        pointService.usePoint(userId, amount);
        Long balanceAfter = point.getPoint();

        // 3. 포인트 이력 생성
        pointHistoryService.createHistories(
                userId, amount, balanceBefore, balanceAfter, TransactionType.USE
        );
    }

    @Transactional
    public void chargePoint(Long userId, Long amount){
        // 1. 포인트 정보 가져오기
        UserPoint point = pointService.getPoint(userId);
        // 2. 포인트 충전
        Long balanceBefore = point.getPoint();
        pointService.chargePoint(userId, amount);
        Long balanceAfter = point.getPoint();

        // 3. 포인트 이력 생성
        pointHistoryService.createHistories(
                userId, amount, balanceBefore, balanceAfter, TransactionType.CHARGE
        );
    }
}
