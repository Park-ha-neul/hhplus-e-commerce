package kr.hhplus.be.server.infrastructure.user;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserErrorCode;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user){
        return userJpaRepository.save(user);
    }

    @Override
    public User findById(Long userId){
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));
    }
}
