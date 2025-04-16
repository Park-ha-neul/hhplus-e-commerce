package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    void 사용자_포인트_조회_성공(){
        User user = User.create(1L, false);
        UserPoint userPoint = UserPoint.createNew(user.getUserId());
        UserPointEntity entity = UserPointEntity.create(user, userPoint);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(entity));
        UserPoint result = userPointService.getUserPoint(user.getUserId());

        assertEquals(Long.valueOf(0L), result.getPoint());
    }

    @Test
    void 사용자_포인트_조회_예외처리(){
        User user = User.create(1L, false);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.getUserPoint(user.getUserId());
        });

        assertTrue(e.getMessage().contains("해당 사용자의 포인트 정보가 없습니다."));
    }

    @Test
    void 포인트_충전_성공(){
        User user = User.create(1L, false);
        UserPoint point = UserPoint.createNew(user.getUserId());
        UserPointEntity entity = UserPointEntity.create(user, point);


        // When (repository를 mock 처리하여 DB에서 조회된 것처럼 처리)
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(entity));

        // When
        userPointService.charge(user.getUserId(), 100L);

        // Then
        assertEquals(Long.valueOf(100L), entity.getPoint().getPoint());
        verify(userPointEntityRepository).save(entity);  // 저장 호출 확인
    }

    @Test
    void 포인트_충전시_사용자_정보없는경우_예외처리(){
        User user = User.create(1L, false);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userPointService.charge(user.getUserId(), 100L);
        });

        assertEquals("사용자 포인트 정보 없음", e.getMessage());
    }

    @Test
    void 포인트_충전시_충전포인트_음수인경우_예외처리(){
        User user = User.create(1L, false);
        UserPoint userpoint = UserPoint.createNew(user.getUserId());
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userpoint)));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.charge(user.getUserId(), -100L);
        });

        assertEquals("충전 금액은 0보다 커야 합니다.", e.getMessage());
    }

    @Test
    void 포인트_충전시_1회_충전한도_초과_예외처리(){
        User user = User.create(1L, false);
        UserPoint userpoint = UserPoint.createNew(user.getUserId());
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userpoint)));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.charge(user.getUserId(), 100001L);
        });

        assertEquals("1회 충전 한도를 초과했습니다.", e.getMessage());
    }

    @Test
    void 포인트_충전시_사용자_한도초과_예외처리(){
        User user = User.create(1L, false);
        UserPoint userpoint = UserPoint.create(user.getUserId(), 1000000L);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userpoint)));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.charge(user.getUserId(), 100L);
        });

        assertEquals("충전 시 보유 포인트가 최대 한도를 초과합니다.", e.getMessage());

    }

    @Test
    void 포인트_사용_성공(){
        User user = User.create(1L, false);
        UserPoint userPoint = UserPoint.create(user.getUserId(), 200L);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userPoint)));

        userPointService.use(user.getUserId(), 100L);

        assertEquals(Long.valueOf(100L), userPoint.getPoint());

    }

    @Test
    void 포인트_사용시_사용자_정보없는경우_예외처리(){
        User user = User.create(1L, false);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            userPointService.use(user.getUserId(), 100L);
        });

        assertEquals("사용자 포인트 정보 없음", e.getMessage());
    }

    @Test
    void 포인트_사용시_사용포인트_음수인경우_예외처리(){
        User user = User.create(1L, false);
        UserPoint userPoint = UserPoint.create(user.getUserId(), 200L);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userPoint)));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.use(user.getUserId(), -100L);
        });

        assertEquals("사용 금액은 0보다 커야 합니다.", e.getMessage());

    }

    @Test
    void 포인트_사용시_잔액부족_예외처리(){
        User user = User.create(1L, false);
        UserPoint userPoint = UserPoint.create(user.getUserId(), 200L);
        when(userPointEntityRepository.findById(user.getUserId())).thenReturn(Optional.of(UserPointEntity.create(user, userPoint)));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.use(user.getUserId(), 300L);
        });

        assertEquals("잔액이 부족합니다.", e.getMessage());
    }
}
