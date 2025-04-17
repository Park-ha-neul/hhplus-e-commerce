package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "user_point")
public class UserPointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long userId;

    @Embedded
    @Getter
    private User user;

    @Embedded
    @Getter
    private UserPoint point;

    public UserPointEntity(User user, UserPoint point) {
        this.user = user;
        this.point = point;
    }
}