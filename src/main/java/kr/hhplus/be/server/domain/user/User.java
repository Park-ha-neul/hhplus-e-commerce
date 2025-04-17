package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
public class User{
    @Getter
    private Long userId;
    private boolean isAdmin;

    public User(boolean isAdmin){
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }
}


