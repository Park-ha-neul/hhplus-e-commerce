package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.common.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    private Long userId;
    private Long amount; // 사용 포인트
    private Long balanceBefore; // 변경 전 잔액
    private Long balanceAfter; // 변경 후 잔액

    @Enumerated(EnumType.STRING)
    private TransactionType type; // charge, use

    public static PointHistory of(Long userId, Long amount, Long balanceBefore, Long balanceAfter, TransactionType type){
        return new PointHistory(null, userId, amount, balanceBefore, balanceAfter, type);
    }
}
