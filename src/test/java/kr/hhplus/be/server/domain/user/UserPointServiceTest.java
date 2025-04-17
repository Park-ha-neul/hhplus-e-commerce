package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.point.PointHistoryEntity;
import kr.hhplus.be.server.domain.point.PointHistoryEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @Mock
    private PointHistoryEntityRepository pointHistoryEntityRepository;

    @InjectMocks
    private UserPointService userPointService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userPointService = new UserPointService(userPointEntityRepository, pointHistoryEntityRepository);
    }

    @Test
    void 포인트_충전_성공() {
        // given
        Long userId = 1L;
        Long amount = 1000L;

        UserPointEntity userPointEntity = mock(UserPointEntity.class);
        PointHistoryEntity pointHistoryEntity = mock(PointHistoryEntity.class);
        UserPoint userPoint = mock(UserPoint.class);

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(userPointEntity));
        when(userPointEntity.getPoint()).thenReturn(userPoint);
        when(pointHistoryEntityRepository.save(any(PointHistoryEntity.class))).thenReturn(pointHistoryEntity);  // Or omit the return value if not necessary

        userPointService.charge(userId, amount);

        verify(userPointEntityRepository).findById(userId);  // Verifying the user lookup
        verify(pointHistoryEntityRepository).save(argThat(entity ->
                entity.getAmount().equals(amount) // Optionally verify the entity state
        ));
        verify(userPointEntityRepository).save(userPointEntity);
    }

    @Test
    void 포인트_사용_성공() {
        // given
        Long userId = 1L;
        Long amount = 1000L;

        UserPointEntity userPointEntity = mock(UserPointEntity.class);
        PointHistoryEntity pointHistoryEntity = mock(PointHistoryEntity.class);
        UserPoint userPoint = mock(UserPoint.class);

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(userPointEntity));
        when(userPointEntity.getPoint()).thenReturn(userPoint);
        when(pointHistoryEntityRepository.save(any(PointHistoryEntity.class))).thenReturn(pointHistoryEntity);  // Or omit the return value if not necessary

        userPointService.use(userId, amount);

        verify(userPointEntityRepository).findById(userId);  // Verifying the user lookup
        verify(pointHistoryEntityRepository).save(argThat(entity ->
                entity.getAmount().equals(amount) // Optionally verify the entity state
        ));
        verify(userPointEntityRepository).save(userPointEntity);
    }

    @Test
    void 포인트_히스토리_조회_성공() {
        // given
        Long userId = 1L;
        PointHistoryEntity pointHistoryEntity = mock(PointHistoryEntity.class);
        List<PointHistoryEntity> pointHistoryList = List.of(pointHistoryEntity);

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(mock(UserPointEntity.class)));
        when(pointHistoryEntityRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(pointHistoryList);

        // when
        List<PointHistoryEntity> result = userPointService.getUserPointHistory(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pointHistoryEntity, result.get(0));
        verify(pointHistoryEntityRepository).findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void 포인트_충전_실패_사용자_없음() {
        // given
        Long userId = 1L;
        Long amount = 1000L;

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userPointService.charge(userId, amount);
        });
        assertEquals(UserPointErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 포인트_사용_실패_사용자_없음() {
        // given
        Long userId = 1L;
        Long amount = 500L;

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userPointService.use(userId, amount);
        });
        assertEquals(UserPointErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 포인트_히스토리_조회_실패_사용자_없음() {
        // given
        Long userId = 1L;

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userPointService.getUserPointHistory(userId);
        });
        assertEquals(UserPointErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
