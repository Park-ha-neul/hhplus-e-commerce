package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class User{
    @Getter
    private Long userId;
    private boolean isAdmin;

    private User(Long userId, boolean isAdmin){
        this.userId = userId;
        this.isAdmin = isAdmin;
    }

    public static User create(Long userId, boolean isAdmin){
        return new User(userId, isAdmin);
    }

    public boolean isAdmin(){
        return isAdmin;
    }
}


