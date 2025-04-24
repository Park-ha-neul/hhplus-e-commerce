package kr.hhplus.be.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResult {
    private Long userId;
    private String userName;
    private boolean isAdmin;
    private Long point;

    public static UserResult of(User user, UserPoint userPoint){
        return new UserResult(
                user.getUserId(),
                user.getUserName(),
                user.isAdmin(),
                userPoint.getPoint()
        );
    }
}
