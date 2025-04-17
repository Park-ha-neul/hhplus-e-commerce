package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class User{
    @Getter
    @Column(name = "admin_yn")
    private Boolean admin;

//    public User(boolean admin){
//        this.admin = admin;
//    }

    public boolean isAdmin() {
        return this.admin;
    }
}


