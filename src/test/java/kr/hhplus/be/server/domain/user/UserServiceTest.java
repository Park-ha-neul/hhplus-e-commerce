package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Test
    void 관리자_여부_확인(){
        User adminUser = User.create(1L, true);

        boolean isAdmin = userService.isAdmin(adminUser);
        assertTrue(isAdmin);
    }
}
