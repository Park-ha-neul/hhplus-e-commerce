package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_point")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserPointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long userId;

    @Embedded
    private User user;

    @Embedded
    private UserPoint point;

    @Builder
    public UserPointEntity(User user, UserPoint point) {
        this.user = user;
        this.point = point;
    }
}