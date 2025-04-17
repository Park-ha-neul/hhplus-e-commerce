package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import lombok.Getter;

@Entity
@Table(name = "point_history")
@Getter
public class PointHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Embedded
    @ManyToOne
    private UserPointEntity userPointEntity;

    private Long amount;
    private Long balanceBefore;
    private Long balanceAfter;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // charge, use

    public PointHistoryEntity(UserPointEntity userPointEntity, Long amount, Long balanceBefore, Long balanceAfter, TransactionType type){
        this.userPointEntity = userPointEntity;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.type = type;
    }

    public static PointHistoryEntity chargeHistory(UserPointEntity userPointEntity, Long amount, Long balanceBefore, Long balanceAfter){
        return new PointHistoryEntity(
                userPointEntity,
                amount,
                balanceBefore,
                balanceAfter,
                TransactionType.CHARGE
        );
    }

    public static PointHistoryEntity useHistory(UserPointEntity userPointEntity, Long amount, Long balanceBefore, Long balanceAfter){
        return new PointHistoryEntity(
                userPointEntity,
                amount,
                balanceBefore,
                balanceAfter,
                TransactionType.USE
        );
    }
}
