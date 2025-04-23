package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@ExtendWith(MockitoExtension.class)
public class UserPointTest {

    @Test
    void 포인트_충전_정상(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        userPoint.charge(200L);
        assertEquals(Long.valueOf(700L), userPoint.getPoint());
    }

    @Test
    void 포인트_충전_시_금액이_0보다_작은경우_예외처리(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.charge(-100L);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    void 포인트_1회충전_한도_초과시_예외처리(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.charge(100001L);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
    }

    @Test
    void 포인트_충전시_보유포인트_한도_초과시_예외처리(){
        UserPoint userPoint = new UserPoint(1L, 1000000L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.charge(100L);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());
    }

    @Test
    void 포인트_사용_정상(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        userPoint.use(100L);
        assertEquals(Long.valueOf(400L), userPoint.getPoint());
    }

    @Test
    void 포인트_사용시_포인트가_음수인경우_예외처리(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.use(-100L);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    void 포인트_사용시_잔액_부족_예외처리(){
        UserPoint userPoint = new UserPoint(1L, 500L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.use(600L);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());
    }
}
