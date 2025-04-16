package kr.hhplus.be.server.application.user.usecase;

import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @InjectMocks
    private User user;

    @Test
    void 사용자_생성_성공(){
        kr.hhplus.be.server.domain.user.User user = kr.hhplus.be.server.domain.user.User.create(1L, false);
        final List<UserPointEntity> savedEntities = new ArrayList<>();

        doAnswer(i -> {
            savedEntities.add(i.getArgument(0));
            return null;
        }).when(userPointEntityRepository).save(any(UserPointEntity.class));

        this.user.registerUser(user);

        verify(userPointEntityRepository, times(1)).save(any(UserPointEntity.class));
        assertEquals(Long.valueOf(0L), savedEntities.get(0).getPoint().getPoint());
    }

}
