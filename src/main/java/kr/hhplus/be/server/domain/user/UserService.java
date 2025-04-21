package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    public User createUser(String userName, boolean isAdmin){
        User user = new User(userName, isAdmin);
        UserPoint userPoint = new UserPoint(user.getUserId(),0L);

        userRepository.save(user);
        userPointRepository.save(userPoint);

        return user;
    }

    public UserWithPointResponse getUser(Long userId){
        User user = userRepository.findById(userId);
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        return new UserWithPointResponse(user, userPoint);
    }
}
