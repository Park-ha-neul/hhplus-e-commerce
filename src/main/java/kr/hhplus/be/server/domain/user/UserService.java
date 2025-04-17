package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserPointEntityRepository userPointEntityRepository;

    public UserPointEntity createUser(boolean isAdmin){
        User user = new User(isAdmin);
        UserPoint userPoint = new UserPoint(0L);

        UserPointEntity userPointEntity = new UserPointEntity(user, userPoint);

        return userPointEntityRepository.save(userPointEntity);
    }

    public Optional<UserPointEntity> getUser(Long userId){
        return userPointEntityRepository.findById(userId);
    }
}
