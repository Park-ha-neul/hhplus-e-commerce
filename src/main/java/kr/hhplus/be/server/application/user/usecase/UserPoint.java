package kr.hhplus.be.server.application.user.usecase;

import kr.hhplus.be.server.domain.point.PointHistoryService;
import kr.hhplus.be.server.domain.user.UserPointService;
import kr.hhplus.be.server.domain.user.UserService;

public class UserPoint {

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint(UserPointService userPointService, PointHistoryService pointHistoryService) {
        this.userPointService = userPointService;
        this.pointHistoryService = pointHistoryService;
    }

    public void UserPointCharge(Long userId, Long amount){
        kr.hhplus.be.server.domain.user.UserPoint userPoint = userPointService.getUserPoint(userId);
        Long balanceBefore = userPoint.getPoint();
        userPoint.charge(amount);
        Long balanceAfter = userPoint.getPoint();
        pointHistoryService.createHistory(userId, amount, balanceBefore, balanceAfter, "CHARGE");

    }

    public void UserPointUse(Long userId, Long amount){
        kr.hhplus.be.server.domain.user.UserPoint userPoint = userPointService.getUserPoint(userId);

        Long balanceBefore = userPoint.getPoint();
        userPoint.use(amount);
        Long balanceAfter = userPoint.getPoint();

        pointHistoryService.createHistory(userId, amount, balanceBefore, balanceAfter, "USE");
    }
}
