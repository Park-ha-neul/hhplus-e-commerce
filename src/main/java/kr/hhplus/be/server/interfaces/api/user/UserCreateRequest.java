package kr.hhplus.be.server.interfaces.api.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    private String userName;
    private boolean isAdmin;
}
