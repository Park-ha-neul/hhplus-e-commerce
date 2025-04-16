package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.TransactionType;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import lombok.Getter;

@Entity
public class PointHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long userId;
    private Long amount; // 사용 포인트
    private Long balanceBefore; // 변경 전 잔액
    private Long balanceAfter; // 변경 후 잔액

    @Getter
    @Enumerated(EnumType.STRING)
    private TransactionType type; // charge, use

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", insertable = false, updatable = false)
    private UserPointEntity userPointEntity; // 관련된 사용자 정보


    public PointHistoryEntity(Long userId, Long amount, Long balanceBefore, Long balanceAfter, TransactionType type){
        this.userId = userId;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.type = type;
    }

    public static PointHistoryEntity createHistory(Long userId, Long amount, Long balanceBefore, Long balanceAfter, String typeName){
        TransactionType type;
        try {
            type = TransactionType.valueOf(typeName);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("존재하지 않는 거래 유형입니다: " + typeName, e);
        }

        return new PointHistoryEntity(
                userId,
                amount,
                balanceBefore,
                balanceAfter,
                type
        );
    }

}
