package kr.hhplus.be.server.application.service.userPoint;

import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    public void 포인트_충전_성공(){
        long userId = 1L;
        long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        // when
        pointService.chargePoint(userId, amount);

        // then
        assertEquals(Long.valueOf(200L), userPoint.getPoint());
    }

    @Test
    public void 충전_시_포인트가_없는경우_예외처리(){
        long userId = 1L;
        long amount = 100L;
        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargePoint(userId, amount);
        });

        assertEquals("사용자의 포인트 정보가 없습니다.", e.getMessage());
    }

    @Test
    public void 충전_시_충전금액이_0보다_작은경우_예외처리(){
        long userId = 1L;
        long amount = -100L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargePoint(userId, amount);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 충전_시_1회충전량_초과한경우_예외처리(){
        long userId = 1L;
        long amount = 100001L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargePoint(userId, amount);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
    }

    @Test
    public void 충전_시_최대한도를_초과한경우_예외처리(){
        long userId = 1L;
        long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 1000000L, false);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.chargePoint(userId, amount);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());
    }

    @Test
    public void 포인트_사용_성공(){
        long userId = 1L;
        long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        pointService.usePoint(userId, amount);

        assertEquals(Long.valueOf(0L), userPoint.getPoint());
    }

    @Test
    public void 사용_시_포인트가_없는경우_예외처리(){
        long userId = 1L;
        long amount = 100L;
        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(userId, amount);
        });

        assertEquals("사용자의 포인트 정보가 없습니다.", e.getMessage());
    }

    @Test
    public void 사용_금액이_음수인경우_예외처리(){
        long userId = 1L;
        long amount = -100L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
           pointService.usePoint(userId, amount);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 사용_시_잔액이_부족한경우_예외처리(){
        long userId = 1L;
        long amount = 200L;
        UserPoint userPoint = new UserPoint(userId, 100L, false);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPoint));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(userId, amount);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());
    }
}
