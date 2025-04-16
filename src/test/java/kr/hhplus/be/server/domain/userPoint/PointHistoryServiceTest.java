package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.point.PointHistoryEntity;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.PointHistoryService;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Test
    void 사용자_포인트_이력_조회_성공(){
        User user = User.create(1L, false);
        pointHistoryService.getUserPointHistory(user.getUserId());

        verify(pointHistoryRepository).findAllByUserIdOrderByCreatedAtDesc(user.getUserId());
    }

    @Test
    void 사용자_충전_이력_저장(){
        pointHistoryService.createHistory(1L, 100L, 100L, 200L, "CHARGE");

        verify(pointHistoryRepository).save(any(PointHistoryEntity.class));
    }

    @Test
    void 사용자_사용_이력_저장(){
        pointHistoryService.createHistory(1L, 200L, 300L, 100L, "USE");

        verify(pointHistoryRepository).save(any(PointHistoryEntity.class));
    }

    @Test
    void 이력_저장시_없는타입으로_저장시_예외처리(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            pointHistoryService.createHistory(1L, 100L, 200L, 300L, "SAVE");
        });
        assertTrue(e.getMessage().contains("존재하지 않는 거래 유형입니다"));
    }
}
