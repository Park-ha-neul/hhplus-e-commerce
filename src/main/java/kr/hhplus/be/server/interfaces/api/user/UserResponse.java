package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.user.UserResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String userName;
    private boolean isAdmin;
    private Long point;

    public static UserResponse from(UserResult userResult) {
        return new UserResponse(userResult.getUserId(), userResult.getUserName(), userResult.isAdmin(),userResult.getPoint());
    }
}
