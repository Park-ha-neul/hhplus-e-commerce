package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "point_history")
@Getter
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "balance_before")
    private Long balanceBefore;

    @Column(name = "balance_after")
    private Long balanceAfter;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // charge, use

    public enum TransactionType {
        CHARGE, USE
    }

    @Builder
    public PointHistory(Long userId, Long amount, Long balanceBefore, Long balanceAfter, TransactionType type){
        this.userId = userId;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.type = type;
    }

    public static PointHistory chargeHistory(Long userId, Long amount, Long balanceBefore, Long balanceAfter){
        return new PointHistory(
                userId,
                amount,
                balanceBefore,
                balanceAfter,
                TransactionType.CHARGE
        );
    }

    public static PointHistory useHistory(Long userId, Long amount, Long balanceBefore, Long balanceAfter){
        return new PointHistory(
                userId,
                amount,
                balanceBefore,
                balanceAfter,
                TransactionType.USE
        );
    }
}
