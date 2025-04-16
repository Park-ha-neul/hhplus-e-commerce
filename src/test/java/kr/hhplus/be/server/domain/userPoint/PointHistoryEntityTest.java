package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.point.PointHistoryEntity;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointHistoryEntityTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    void 충전_포인트_이력_생성_성공(){
        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.createHistory(1L, 100L, 100L, 200L, "CHARGE");
        assertEquals(TransactionType.CHARGE, pointHistoryEntity.getType());
    }

    @Test
    void 사용_포인트_이력_생성_성공(){
        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.createHistory(1L, 100L, 200L, 100L, "USE");
        assertEquals(TransactionType.USE, pointHistoryEntity.getType());
    }

    @Test
    void 사용자_포인트_이력_생성시_없는타입인경우_예외처리(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            PointHistoryEntity.createHistory(1L, 100L, 200L, 300L, "SAVE");
        });
        assertTrue(e.getMessage().contains("존재하지 않는 거래 유형입니다"));
    }

    @Test
    void 사용자_포인트_이력_조회(){
        Long userId = 1L;

        PointHistoryEntity history1 = new PointHistoryEntity(userId, 100L, 200L, 100L, TransactionType.USE);
        PointHistoryEntity history2 = new PointHistoryEntity(userId, 200L, 300L, 100L, TransactionType.CHARGE);

        // 최신순으로 정렬된 리스트 생성
        List<PointHistoryEntity> mockList = List.of(history2, history1);

        // mocking
        when(pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(mockList);

        // when
        List<PointHistoryEntity> result = pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        // then
        assertEquals(2, result.size());
    }
}
