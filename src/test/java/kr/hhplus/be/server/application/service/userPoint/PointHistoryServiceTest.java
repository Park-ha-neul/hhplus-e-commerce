package kr.hhplus.be.server.application.service.userPoint;

import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {
    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Test
    public void 사용자_포인트_이력_조회시_데이터가_있는경우(){
        Long userId = 1L;
        PointHistory history1 = PointHistory.of(userId, 100L, 200L, 100L, TransactionType.USE);
        PointHistory history2 = PointHistory.of(userId, 200L, 300L, 100L, TransactionType.CHARGE);

        // 최신순으로 정렬된 리스트 생성
        List<PointHistory> mockList = List.of(history2, history1);

        // mocking
        when(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockList);

        // when
        List<PointHistory> result = pointHistoryService.getHistories(userId);

        // then
        assertEquals(2, result.size());
        assertEquals(history2.getAmount(), result.get(0).getAmount()); // 가장 최근 것부터
        assertEquals(history1.getAmount(), result.get(1).getAmount());
    }

    @Test
    public void 사용자_포인트_이력_조회시_데이터가_없는경우(){
        Long userId = 1L;
        when(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());

        //when
        List<PointHistory> result = pointHistoryService.getHistories(userId);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void 포인트_이력_등록_성공(){
        Long userId = 1L;
        Long amount = 100L;
        Long balanceBefore = 200L;
        Long balanceAfter = 100L;
        TransactionType type = TransactionType.USE;

        PointHistory dummyHistory = PointHistory.of(userId, amount, balanceBefore, balanceAfter, type);
        // mocking
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(dummyHistory);

        // when
        PointHistory pointHistory = pointHistoryService.createHistories(userId, amount, balanceBefore, balanceAfter, type);

        // then
        assertEquals(userId, pointHistory.getUserId());
        assertEquals(amount, pointHistory.getAmount());
        assertEquals(balanceBefore, pointHistory.getBalanceBefore());
        assertEquals(balanceAfter, pointHistory.getBalanceAfter());
        assertEquals(type, pointHistory.getType());
    }

    @Test
    public void 포인트_이력_등록시_필드_예외처리(){
        Long userId = null;
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointHistoryService.createHistories(userId, 100L, 200L, 100L, TransactionType.USE);
        });

        assertEquals("모든 필드를 입력해야 합니다.", e.getMessage());
    }
}
