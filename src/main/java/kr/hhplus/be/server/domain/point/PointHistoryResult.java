package kr.hhplus.be.server.domain.point;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointHistoryResult {
    private Long id;
    private Long amount;
    private Long balanceBefore;
    private Long balanceAfter;
    private PointHistory.TransactionType type;

    public static PointHistoryResult of(PointHistory pointHistory) {
        return new PointHistoryResult(
                pointHistory.getHistoryId(),
                pointHistory.getAmount(),
                pointHistory.getBalanceBefore(),
                pointHistory.getBalanceAfter(),
                pointHistory.getType()
        );
    }
}
