package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class BalanceTest {

    @Test
    public void 재고_정상_증가() {
        Long amount = 1L;
        Balance balance = Balance.create(2L);
        balance.increase(amount);

        assertEquals(Long.valueOf(3L), balance.getQuantity());
    }

    @Test
    public void 재고_증가량_음수인_경우_예외처리() {
        Long amount = -2L;
        Balance balance = Balance.create(2L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            balance.increase(amount);
        });

        assertEquals("증가량은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 재고_정상_차감() {
        Long amount = 1L;
        Balance balance = Balance.create(2L);
        balance.decrease(amount);

        assertEquals(Long.valueOf(1L), balance.getQuantity());
    }

    @Test
    public void 재고_차감시_재고부족_예외처리(){
        Long amount = 2L;
        Balance balance = Balance.create(1L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            balance.decrease(amount);
        });

        assertEquals("재고 부족", e.getMessage());
    }
    @Test
    public void 재고_차감시_음수인_경우_예외처리(){
        Long amount = -1L;
        Balance balance = Balance.create(1L);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            balance.decrease(amount);
        });

        assertEquals("차감량은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    public void 재고가_없을때_상태값_전달(){
        Balance balance = Balance.create(0L);
        assertTrue(balance.isSoldOut());
    }
}