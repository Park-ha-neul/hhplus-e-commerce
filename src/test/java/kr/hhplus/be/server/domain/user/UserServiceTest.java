package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Test
    void 사용자_유저_생성(){
        User user = new User("하늘", false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResult result = userService.createUser("하늘", false);

        assertFalse(result.isAdmin());
        assertEquals("하늘", result.getUserName());
        assertEquals(Long.valueOf(0L), result.getPoint());
        verify(userRepository).save(any(User.class));
        verify(userPointRepository).save(any(UserPoint.class));
    }

    @Test
    void 관리자_유저_생성(){
        User user = new User("관리자", true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResult result = userService.createUser("관리자", true);

        assertTrue(result.isAdmin());
        assertEquals("관리자", result.getUserName());
        assertEquals(Long.valueOf(0L), result.getPoint());
        verify(userRepository).save(any(User.class));
        verify(userPointRepository).save(any(UserPoint.class));
    }

    @Test
    void 사용자_정보_조회_있는_사용자인_경우(){
        Long userId = 1L;
        User mockUser = User.builder()
                .userName("하늘")
                .adminYn(false)
                .build();
        ReflectionTestUtils.setField(mockUser, "userId", userId);

        UserPoint mockUserPoint = new UserPoint(userId, 0L);

        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(userPointRepository.findByUserId(userId)).thenReturn((mockUserPoint));

        UserResult result = userService.getUser(userId);

        assertEquals("하늘", result.getUserName());
        assertFalse(result.isAdmin());
        assertEquals(Long.valueOf(0L), result.getPoint());
    }

    @Test
    void 사용자_정보_조회_없는_사용자인_경우_EntityNotFoundException_반환(){
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(null);

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUser(userId);
        });

        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), e.getMessage());
    }
}
