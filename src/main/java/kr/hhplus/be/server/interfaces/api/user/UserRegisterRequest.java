package kr.hhplus.be.server.interfaces.api.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserRegisterRequest {
    private boolean isAdmin;
}
