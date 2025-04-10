package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.point.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPoint extends BaseEntity {
    @Id
    private Long userId;

    private Long point;
    private boolean adminYN;

    public static UserPoint from(User user) {
        return new UserPoint(user.getUserId(), user.getPoint(), user.isAdminYN());
    }

    public void chargePoint(Long amount){
        Point point = new Point(this.userId, this.point);
        point.charge(amount);
        this.point = point.getPoint();
    }

    public void usePoint(Long amount){
        Point point = new Point(this.userId, this.point);
        point.use(amount);
        this.point = point.getPoint();
    }
}
