package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserPointTest {

    @Test
    public void 정상_충전(){
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);

        // when
        userPoint.chargePoint(amount);

        // then
        assertEquals(Long.valueOf(200L), userPoint.getPoint());
    }

    @Test
    public void 충전량_음수인_경우_예외처리(){
        Long amount = -100L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.chargePoint(amount);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 일회_충전량_초과_예외처리(){
        Long amount = 100001L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.chargePoint(amount);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
    }

    @Test
    public void 충전_최대_한도_초과_예외처리(){
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(1L, 1000000L, false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.chargePoint(amount);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());
    }

    @Test
    public void 정상_사용(){
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(1L, 200L, false);

        userPoint.usePoint(amount);

        assertEquals(Long.valueOf(100L), userPoint.getPoint());
    }

    @Test
    public void 포인트_사용_금액_음수인_경우_예외처리(){
        Long amount = -100L;
        UserPoint userPoint = new UserPoint(1L, 200L,false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.usePoint(amount);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());
    }
    @Test
    public void 포인트_부족_사용_예외처리(){
        Long amount = 200L;
        UserPoint userPoint = new UserPoint(1L, 100L, false);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.usePoint(amount);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());

    }
}

