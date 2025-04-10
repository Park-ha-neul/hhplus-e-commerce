package kr.hhplus.be.server.application.service.userPoint;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void 사용자_조회_성공(){
        long userId = 1L;
        UserPoint mockUserPoint = new UserPoint(userId, 100L, false);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(mockUserPoint));

        UserPoint result = userService.getUser(userId);

        assertEquals(Long.valueOf(userId), result.getUserId());
    }

    @Test
    public void 사용자_조회_시_사용자가_없는경우_예외처리(){
        long userId = 1L;

        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(userId);
        });

        assertEquals("사용자를 찾을 수 없습니다.", e.getMessage());
    }

    @Test
    public void 관리자_여부_체크_성공(){
        long userId = 1L;
        UserPoint mockUserPoint = new UserPoint(userId, 100L, true);
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(mockUserPoint));

        assertTrue(userService.isAdmin(userId));
    }

    @Test
    public void 관리자_여부_체크_시_사용자가_없는경우_예외처리(){
        long userId = 1L;
        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userService.isAdmin(userId);
        });

        assertEquals("사용자를 찾을 수 없습니다.", e.getMessage());
    }

    @Test
    public void 일반_사용자_등록_성공(){
        long userId = 1L;
        boolean adminYN = false;
        User mockUser = User.createUser(userId, adminYN);
        UserPoint mockUserPoint = UserPoint.from(mockUser);

        when(userPointRepository.existsById(userId)).thenReturn(false);
        // 저장 동작 mocking
        when(userPointRepository.save(any(UserPoint.class))).thenReturn(mockUserPoint);

        UserPoint result = userService.createUser(userId, adminYN);

        assertEquals(Long.valueOf(userId), result.getUserId());
        assertEquals(Long.valueOf(0L), result.getPoint());
        assertFalse(result.isAdminYN());
    }

    @Test
    public void 관리자_사용자_등록_성공(){
        long userId = 1L;
        boolean adminYN = true;
        User mockUser = User.createUser(userId, adminYN);
        UserPoint mockUserPoint = UserPoint.from(mockUser);

        when(userPointRepository.existsById(userId)).thenReturn(false);
        when(userPointRepository.save(any(UserPoint.class))).thenReturn(mockUserPoint);

        UserPoint result = userService.createUser(userId, adminYN);

        assertEquals(Long.valueOf(userId), result.getUserId());
        assertEquals(Long.valueOf(0L), result.getPoint());
        assertTrue(result.isAdminYN());
    }

    @Test
    public void 사용자_등록시_이미_있는_사용자인_경우_예외처리(){
        long userId = 1L;

        when(userPointRepository.existsById(userId)).thenReturn(true);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(userId, false);
        });
        assertEquals("이미 존재하는 사용자 입니다.", e.getMessage());

    }
}
