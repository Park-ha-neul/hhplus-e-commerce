package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    void 관리자_등록_및_관리자_여부_확인(){
        User user = new User("하늘", true);
        assertTrue(user.isAdmin());
    }

    @Test
    void 사용자_등록_및_관리자_여부_확인(){
        User user = new User("하늘", false);
        assertFalse(user.isAdmin());
    }
}
