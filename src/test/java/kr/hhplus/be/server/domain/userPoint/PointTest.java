package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.point.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PointTest {

    @Test
    public void 포인트_충전_성공(){
        Point point = new Point(1L, 100L);

        point.charge(100L);

        assertEquals(Long.valueOf(200L), point.getPoint());
    }

    @Test
    public void 충전_금액이_0보다_작은_경우(){
        long amount = -100L;
        Point point = new Point(1L, 100L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            point.charge(amount);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 일회_충전_금액을_초과한_경우(){
        long amount = 100001L;
        Point point = new Point(1L, 100L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            point.charge(amount);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
    }

    @Test
    public void 충전_시_보유_포인트가_최대한도를_초과한_경우(){
        long amount = 100L;
        Point point = new Point(1L, 1000000L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            point.charge(amount);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());
    }

    @Test
    public void 포인트_사용_성공(){
        long amount = 100L;
        Point point = new Point(1L, 200L);

        point.use(amount);

        assertEquals(Long.valueOf(100L), point.getPoint());
    }

    @Test
    public void 사용_금액이_0보다_작은_경우(){
        long amount = -100L;
        Point point = new Point(1L, 200L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            point.use(amount);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 사용_잔액이_부족한_경우(){
        long amount = 200L;
        Point point = new Point(1L, 100L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            point.use(amount);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());
    }
}
