package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_point")
public class UserPointEntity {

    @Embedded
    @Getter
    private User user;

    @Embedded
    @Getter
    private UserPoint point;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private UserPointEntity(User user, UserPoint point) {
        this.userId = user.getUserId();
        this.user = user;
        this.point = point;
    }

    public static UserPointEntity create(User user, UserPoint point){
        return new UserPointEntity(user, point);
    }
}
