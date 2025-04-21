package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PointHistoryTest {

    @Test
    void 포인트_사용_이력_쌓기(){
        Long userId = 1L;
        Long amount = 100L;
        Long balanceBefore = 100L;
        Long balanceAfter = 200L;
        PointHistory pointHistory = PointHistory.useHistory(userId, amount, balanceBefore, balanceAfter);

        assertEquals(PointHistory.TransactionType.USE, pointHistory.getType());
    }

    @Test
    void 포인트_충전_이력_쌓기(){
        Long userId = 1L;
        Long amount = 100L;
        Long balanceBefore = 100L;
        Long balanceAfter = 0L;
        PointHistory pointHistory = PointHistory.chargeHistory(userId, amount, balanceBefore, balanceAfter);

        assertEquals(PointHistory.TransactionType.CHARGE, pointHistory.getType());
    }
}
