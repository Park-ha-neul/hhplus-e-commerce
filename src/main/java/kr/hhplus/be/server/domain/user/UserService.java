package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.coupon.ErrorCode;
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
        if(user == null){
            throw new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        return new UserWithPointResponse(user, userPoint);
    }
}
