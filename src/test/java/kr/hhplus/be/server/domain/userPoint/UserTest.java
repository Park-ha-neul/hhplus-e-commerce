package kr.hhplus.be.server.domain.userPoint;

import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    public void 일반_사용자_등록_성공(){
        User user = User.createUser(1L, false);

        assertEquals(Long.valueOf(1L), user.getUserId());
        assertEquals(Long.valueOf(0L), user.getPoint());
        assertFalse(user.isAdminYN());
    }

    @Test
    public void 관리자_등록_성공(){
        User user = User.createUser(1L, true);

        assertEquals(Long.valueOf(1L), user.getUserId());
        assertEquals(Long.valueOf(0L), user.getPoint());
        assertTrue(user.isAdminYN());
    }
}
