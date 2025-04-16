package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    void 관리자_등록_및_관리자_여부_확인(){
        User user = User.create(1L, true);
        assertTrue(user.isAdmin());
    }
}
