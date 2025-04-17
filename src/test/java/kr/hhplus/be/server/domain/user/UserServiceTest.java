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
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @Test
    void 사용자_유저_생성(){
        User user = new User(false);
        UserPoint userPoint = new UserPoint(0L);
        UserPointEntity mockUserPoinEntity = new UserPointEntity(user, userPoint);
        when(userPointEntityRepository.save(any(UserPointEntity.class))).thenReturn(mockUserPoinEntity);

        UserPointEntity result = userService.createUser(false);

        assertFalse(result.getUser().isAdmin());
        verify(userPointEntityRepository).save(any(UserPointEntity.class));
    }

    @Test
    void 관리자_유저_생성(){
        User user = new User(true);
        UserPoint userPoint = new UserPoint(0L);
        UserPointEntity mockUserPoinEntity = new UserPointEntity(user, userPoint);
        when(userPointEntityRepository.save(any(UserPointEntity.class))).thenReturn(mockUserPoinEntity);

        UserPointEntity result = userService.createUser(true);

        assertTrue(result.getUser().isAdmin());
        verify(userPointEntityRepository).save(any(UserPointEntity.class));
    }

    @Test
    void 사용자_정보_조회(){
        User user = new User(false);
        UserPoint userPoint = new UserPoint(100L);
        UserPointEntity mockEntity = new UserPointEntity(user, userPoint);

        when(userPointEntityRepository.findById(anyLong())).thenReturn(Optional.of(mockEntity));

        Optional<UserPointEntity> result = userService.getUser(1L);

        assertEquals(Long.valueOf(100L), result.get().getPoint().getPoint());
        verify(userPointEntityRepository).findById(1L);
    }

}
