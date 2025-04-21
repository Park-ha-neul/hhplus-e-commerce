package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

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

    @Mock
    private UserJpaRepository userJpaRepository;

    @Test
    void 사용자_유저_생성(){
        User user = new User("하늘", false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser("하늘", false);

        assertFalse(result.isAdmin());
        assertEquals("하늘", result.getUserName());
        verify(userRepository).save(any(User.class));
        verify(userPointRepository).save(any(UserPoint.class));
    }

    @Test
    void 관리자_유저_생성(){
        User user = new User("관리자", false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser("관리자", false);

        assertTrue(result.isAdmin());
        assertEquals("관리자", result.getUserName());
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

        when(userRepository.findById(userId)).thenReturn(mockUser);

        UserWithPointResponse result = userService.getUser(userId);

        assertEquals("하늘", result.getUser().getUserName());
        assertFalse(result.getUser().isAdmin());
    }

    @Test
    void 사용자_정보_조회_없는_사용자인_경우_EntityNotFoundException_반환(){
        Long userId = 999L;
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUser(userId)
        );

        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), e.getMessage());
    }

}
