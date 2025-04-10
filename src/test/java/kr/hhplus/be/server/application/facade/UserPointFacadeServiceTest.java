package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.application.service.userPoint.PointHistoryService;
import kr.hhplus.be.server.application.service.userPoint.PointService;
import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointFacadeServiceTest {

    @Mock
    private PointService pointService;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private UserPointFacadeService userPointFacadeService;

    @Test
    public void 포인트_사용_성공(){
        Long userId = 1L;
        Long amount = 100L;
        // given
        UserPoint userPoint = new UserPoint(userId, 500L, false);
        Long balanceBefore = userPoint.getPoint();
        when(pointService.getPoint(userId)).thenReturn(userPoint);

        // 포인트 차감 후 사용자 객체에서 포인트가 줄어든다고 가정
        doAnswer(invocation -> {
            userPoint.setPoint(400L);
            return null;
        }).when(pointService).usePoint(userId, amount);

        //when
        userPointFacadeService.usePoint(userId, amount);

        //then
        Long balanceAfter = userPoint.getPoint();

        verify(pointService).getPoint(userId);
        verify(pointService).usePoint(userId, amount);
        verify(pointHistoryService).createHistories(userId, amount, balanceBefore, balanceAfter, TransactionType.USE);
    }

    @Test
    public void 포인트_사용시_잔고부족_예외처리(){
        Long userId = 1L;
        Long amount = 200L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);

        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doThrow(new IllegalArgumentException("잔액이 부족합니다."))
                .when(pointService).usePoint(userId, amount);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.usePoint(userId, amount);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).usePoint(userId, amount);
        verify(pointHistoryService, never()).createHistories(userId, amount, 100L, 200L, TransactionType.USE);
    }

    @Test
    public void 포인트_사용시_포인트_음수_예외처리(){
        Long userId = 1L;
        Long amount = -100L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);

        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doThrow(new IllegalArgumentException("사용 금액은 0보다 커야 합니다."))
                .when(pointService).usePoint(userId, amount);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.usePoint(userId, amount);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).usePoint(userId, amount);
        verify(pointHistoryService, never()).createHistories(userId, amount, 100L, 200L, TransactionType.USE);
    }

    @Test
    public void 포인트_사용시_이력_쌓기_실패_예외처리(){
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = mock(UserPoint.class);

        when(userPoint.getPoint()).thenReturn(null, 400L); // before: null, after: 400
        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doNothing().when(pointService).usePoint(userId, amount);

        doThrow(new IllegalArgumentException("모든 필드를 입력해야 합니다."))
                .when(pointHistoryService)
                .createHistories(eq(userId), eq(amount), isNull(), eq(400L), eq(TransactionType.USE));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.usePoint(userId, amount);
        });

        assertEquals("모든 필드를 입력해야 합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).usePoint(userId, amount);
        verify(pointHistoryService).createHistories(eq(userId), eq(amount), isNull(), eq(400L), eq(TransactionType.USE));
    }

    @Test
    public void 포인트_충전_성공(){
        Long userId = 1L;
        Long amount = 100L;
        // given
        UserPoint userPoint = new UserPoint(1L, 100L, false);
        Long balanceBefore = userPoint.getPoint();
        when(pointService.getPoint(userId)).thenReturn(userPoint);


        // 포인트 충전 후 사용자 객체에서 포인트가 증가한다고 가정
        doAnswer(invocation -> {
            userPoint.setPoint(200L);
            return null;
        }).when(pointService).chargePoint(userId, amount);

        // when
        userPointFacadeService.chargePoint(userId, amount);

        //then
        Long balanceAfter = userPoint.getPoint();

        verify(pointService).getPoint(userId);
        verify(pointService).chargePoint(userId, amount);
        verify(pointHistoryService).createHistories(userId, amount, balanceBefore, balanceAfter, TransactionType.CHARGE);
    }

    @Test
    public void 포인트_충전_음수_예외처리(){
        Long userId = 1L;
        Long amount = -100L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);

        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doThrow(new IllegalArgumentException("충전 금액은 0보다 커야 합니다."))
                .when(pointService).chargePoint(userId, amount);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.chargePoint(userId, amount);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).chargePoint(userId, amount);
        verify(pointHistoryService, never()).createHistories(userId, amount, 100L, 200L, TransactionType.CHARGE);

    }

    @Test
    public void 포인트_충전_실패_1회_충전량_초과_예외처리(){
        Long userId = 1L;
        Long amount = 100001L;

        UserPoint userPoint = new UserPoint(userId, 100L, false);
        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doThrow(new IllegalArgumentException("1회 충전 한도를 초과했습니다."))
                .when(pointService).chargePoint(userId, amount);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.chargePoint(userId, amount);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).chargePoint(userId, amount);
        verify(pointHistoryService, never()).createHistories(userId, amount, 100L, 200L, TransactionType.CHARGE);
    }

    @Test
    public void 포인트_충전시_최대_한도_초과_예외처리(){
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 1000000L, false);

        when(pointService.getPoint(userId)).thenReturn(userPoint);
        doThrow(new IllegalArgumentException("충전 시 보유 포인트가 최대 한도를 초과합니다."))
                .when(pointService).chargePoint(userId, amount);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->{
            userPointFacadeService.chargePoint(userId, amount);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).chargePoint(userId, amount);
        verify(pointHistoryService, never()).createHistories(userId, amount, 100L, 100L, TransactionType.CHARGE);
    }

    @Test
    public void 포인트_충전시_이력_쌓기_실패_예외처리(){
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = mock(UserPoint.class);

        when(userPoint.getPoint()).thenReturn(null, 400L);
        when(pointService.getPoint(userId)).thenReturn(userPoint);

        doNothing().when(pointService).chargePoint(userId, amount);

        doThrow(new IllegalArgumentException("모든 필드를 입력해야 합니다."))
                .when(pointHistoryService)
                .createHistories(eq(userId), eq(amount), isNull(), eq(400L), eq(TransactionType.CHARGE));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointFacadeService.chargePoint(userId, amount);
        });

        assertEquals("모든 필드를 입력해야 합니다.", e.getMessage());
        verify(pointService).getPoint(userId);
        verify(pointService).chargePoint(userId, amount);
        verify(pointHistoryService).createHistories(eq(userId), eq(amount), isNull(), eq(400L), eq(TransactionType.CHARGE));
    }
}
