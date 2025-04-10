package kr.hhplus.be.server.application.service.userPoint;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserPointRepository userPointRepository;

    public UserPoint getUser(Long userId){
        return userPointRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public boolean isAdmin(Long userId) {
        UserPoint userPoint = userPointRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return userPoint.isAdminYN();
    }

    public UserPoint createUser(Long userId, boolean adminYN){
        if(userPointRepository.existsById(userId)){
            throw new IllegalArgumentException("이미 존재하는 사용자 입니다.");
        }

        User user = User.createUser(userId, adminYN);
        UserPoint entity = UserPoint.from(user);
        return userPointRepository.save(entity);
    }

}
