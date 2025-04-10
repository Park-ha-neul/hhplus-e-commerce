package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User{
    private Long userId;
    private Long point;
    private boolean adminYN;

    public static User createUser(Long userId, boolean isAdmin){
        return new User(userId, 0L, isAdmin);
    }
}


