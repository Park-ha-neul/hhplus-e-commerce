package kr.hhplus.be.server.application.user.usecase;

import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;

public class User {

    private final UserPointEntityRepository userPointEntityRepository;

    public User(UserPointEntityRepository userPointEntityRepository) {
        this.userPointEntityRepository = userPointEntityRepository;
    }

    public void registerUser(kr.hhplus.be.server.domain.user.User user){
        UserPoint userPoint = UserPoint.createNew(user.getUserId());

        UserPointEntity userPointEntity = UserPointEntity.create(user, userPoint);
        userPointEntityRepository.save(userPointEntity);
    }
}
