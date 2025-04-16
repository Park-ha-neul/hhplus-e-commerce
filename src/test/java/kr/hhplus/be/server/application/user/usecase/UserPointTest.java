package kr.hhplus.be.server.application.user.usecase;

import kr.hhplus.be.server.domain.point.PointHistoryService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointTest {

    @Mock
    private UserPointService userPointService;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private UserPoint userPoint;

    @Test
    void 포인트_충전후_이력저장(){
        User user = User.create(1L, false);
        kr.hhplus.be.server.domain.user.UserPoint userPoint = kr.hhplus.be.server.domain.user.UserPoint.createNew(user.getUserId());
        when(userPointService.getUserPoint(user.getUserId())).thenReturn(userPoint);
        this.userPoint.UserPointCharge(user.getUserId(), 100L);

        verify(pointHistoryService).createHistory(user.getUserId(), 100L, 0L, 100L, "CHARGE");
    }

    @Test
    void 포인트_충전시_예외발생(){
        User user = User.create(1L, false);
        kr.hhplus.be.server.domain.user.UserPoint userPoint = kr.hhplus.be.server.domain.user.UserPoint.createNew(user.getUserId());
        when(userPointService.getUserPoint(user.getUserId())).thenReturn(userPoint);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            this.userPoint.UserPointCharge(user.getUserId(), -100L);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    void 포인트_사용후_이력저장(){
        User user = User.create(1L, false);
        kr.hhplus.be.server.domain.user.UserPoint userpoint = kr.hhplus.be.server.domain.user.UserPoint.create(user.getUserId(), 200L);
        when(userPointService.getUserPoint(user.getUserId())).thenReturn(userpoint);

        userPoint.UserPointUse(user.getUserId(), 100L);

        verify(pointHistoryService).createHistory(user.getUserId(), 100L, 200L, 100L, "USE");
    }

    @Test
    void 포인트_사용시_예외발생(){
        User user = User.create(1L, false);
        kr.hhplus.be.server.domain.user.UserPoint userPoint = kr.hhplus.be.server.domain.user.UserPoint.createNew(user.getUserId());
        when(userPointService.getUserPoint(user.getUserId())).thenReturn(userPoint);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            this.userPoint.UserPointUse(user.getUserId(), -100L);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());

    }

}
