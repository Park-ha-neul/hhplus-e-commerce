package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointHistoryTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    public void 포인트_이력_생성_성공(){
        PointHistory pointHistory = PointHistory.of(1L, 100L, 200L, 100L, TransactionType.USE);

        assertEquals(Long.valueOf(1L), pointHistory.getUserId());
        assertEquals(Long.valueOf(100L), pointHistory.getAmount());
        assertEquals(Long.valueOf(200L), pointHistory.getBalanceBefore());
        assertEquals(Long.valueOf(100L), pointHistory.getBalanceAfter());
        assertEquals(TransactionType.USE, pointHistory.getType());
    }

    @Test
    public void 사용자_포인트_이력_조회(){
        Long userId = 1L;

        PointHistory history1 = PointHistory.of(userId, 100L, 200L, 100L, TransactionType.USE);
        PointHistory history2 = PointHistory.of(userId, 200L, 300L, 100L, TransactionType.CHARGE);

        // 최신순으로 정렬된 리스트 생성
        List<PointHistory> mockList = List.of(history2, history1);

        // mocking
        when(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockList);

        // when
        List<PointHistory> result = pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(history2.getAmount(), result.get(0).getAmount()); // 가장 최근 것부터
        assertEquals(history1.getAmount(), result.get(1).getAmount());

    }
}
