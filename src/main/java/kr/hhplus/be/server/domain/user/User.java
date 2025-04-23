package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "name")
    private String userName;

    @Column(name = "admin_yn")
    private Boolean adminYn;

    @Builder
    public User(String userName, boolean adminYn){
        this.userName = userName;
        this.adminYn = adminYn;
    }

    public boolean isAdmin() {
        return this.adminYn;
    }
}


