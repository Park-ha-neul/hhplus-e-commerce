package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.PointHistoryResult;
import kr.hhplus.be.server.interfaces.api.user.UserPointResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    void 포인트_충전_성공() {
        // given
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 200L);

        when(userPointRepository.findByUserId(userId)).thenReturn(userPoint);

        // when
        userPointService.charge(userId, amount);

        // then
        assertEquals(Long.valueOf(300L), userPoint.getPoint());
        verify(userPointRepository).save(userPoint);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    void 포인트_사용_성공() {
        // given
        Long userId = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(userId, 200L);

        when(userPointRepository.findByUserId(userId)).thenReturn(userPoint);

        // when
        userPointService.use(userId, amount);

        // then
        assertEquals(Long.valueOf(100L), userPoint.getPoint());
        verify(userPointRepository).save(userPoint);
        verify(pointHistoryRepository).save(any(PointHistory.class));
    }

    @Test
    void 포인트_히스토리_조회_성공() {
        // given
        Long userId = 1L;
        List<PointHistory> historyList = List.of(
                new PointHistory(userId, 100L, 0L, 100L, PointHistory.TransactionType.CHARGE),
                new PointHistory(userId, 50L, 100L, 50L, PointHistory.TransactionType.USE)
        );

        when(pointHistoryRepository.findByUserId(userId)).thenReturn(historyList);

        // when
        List<PointHistoryResult> result = userPointService.getUserPointHistory(userId);
        List<UserPointResponse> userPointResponseList = UserPointResponse.from(result);

        // then
        assertEquals(2, userPointResponseList.size());
        assertEquals(Long.valueOf(100L), userPointResponseList.get(0).getAmount());
        verify(pointHistoryRepository).findByUserId(userId);
    }
}
