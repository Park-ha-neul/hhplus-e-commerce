package kr.hhplus.be.server.domain.user;

import lombok.Getter;

@Getter
public class UserWithPointResponse {
    private User user;
    private UserPoint userPoint;

    public UserWithPointResponse(User user, UserPoint userPoint){
        this.user = user;
        this.userPoint = userPoint;
    }
}
