package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    public UserResult createUser(String userName, boolean isAdmin){
        User user = new User(userName, isAdmin);
        UserPoint userPoint = new UserPoint(user.getUserId(),0L);

        userRepository.save(user);
        userPointRepository.save(userPoint);

        return UserResult.of(user, userPoint);
    }

    public UserResult getUser(Long userId){
        User user = userRepository.findById(userId);
        if(user == null){
            throw new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
        UserPoint userPoint = userPointRepository.findByUserId(userId);

        return UserResult.of(user, userPoint);
    }
}
