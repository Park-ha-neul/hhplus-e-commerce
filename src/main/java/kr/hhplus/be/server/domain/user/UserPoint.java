package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class UserPoint{
    private Long userId;
    private Long point;

    private static final Long MAX_CHARGE_PER_ONCE = 100_000L;
    private static final Long MAX_TOTAL_POINT = 1_000_000L;

    private UserPoint(Long userId, Long point){
        this.userId = userId;
        this.point = point;
    }

    public static UserPoint create(Long userId, Long point){
        return new UserPoint(userId, point);
    }

    public static UserPoint createNew(Long userId){
        return new UserPoint(userId, 0L);
    }

    public void charge(Long amount){
        if(amount <= 0){
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        if(amount > MAX_CHARGE_PER_ONCE){
            throw new IllegalArgumentException("1회 충전 한도를 초과했습니다.");
        }
        if(this.point + amount > MAX_TOTAL_POINT){
            throw new IllegalArgumentException("충전 시 보유 포인트가 최대 한도를 초과합니다.");
        }

        this.point += amount;
    }

    public void use(Long amount){
        if (amount <= 0){
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.point < amount){
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.point -= amount;
    }
}
