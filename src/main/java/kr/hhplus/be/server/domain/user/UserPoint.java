package kr.hhplus.be.server.domain.user;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class UserPoint{
    private Long point;

    private static final Long MAX_CHARGE_PER_ONCE = 100_000L;
    private static final Long MAX_TOTAL_POINT = 1_000_000L;

    public UserPoint(Long point){
        this.point = point;
    }

    public void charge(Long amount){
        if(amount <= 0){
            throw new IllegalArgumentException(UserPointErrorCode.CHARGE_AMOUNT_MUST_BE_POSITIVE.getMessage());
        }
        if(amount > MAX_CHARGE_PER_ONCE){
            throw new IllegalArgumentException(UserPointErrorCode.CHARGE_AMOUNT_LIMIT_EXCEEDED.getMessage());
        }
        if(this.point + amount > MAX_TOTAL_POINT){
            throw new IllegalArgumentException(UserPointErrorCode.TOTAL_POINT_LIMIT_EXCEEDED.getMessage());
        }

        this.point += amount;
    }

    public void use(Long amount){
        if (amount <= 0){
            throw new IllegalArgumentException(UserPointErrorCode.USAGE_AMOUNT_MUST_BE_POSITIVE.getMessage());
        }
        if (this.point < amount){
            throw new IllegalArgumentException(UserPointErrorCode.INSUFFICIENT_BALANCE.getMessage());
        }
        this.point -= amount;
    }
}
