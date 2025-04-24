package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;

    @Transactional
    public UserResult createUser(String userName, boolean isAdmin){
        try{
            User user = new User(userName, isAdmin);
            UserPoint userPoint = new UserPoint(user.getUserId(),0L);

            userRepository.save(user);
            userPointRepository.save(userPoint);

            return UserResult.of(user, userPoint);
        } catch (Exception e){
            throw new IllegalArgumentException(UserErrorCode.USER_CREATE_ERROR.getMessage());
        }
    }

    public User getUserEntity(Long userId){
        return userRepository.findById(userId);
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
