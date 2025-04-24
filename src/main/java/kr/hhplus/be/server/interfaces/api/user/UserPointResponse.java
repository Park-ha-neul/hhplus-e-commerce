package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryResult;
import kr.hhplus.be.server.domain.user.UserResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserPointResponse {
    private Long id;
    private Long amount;
    private Long balanceBefore;
    private Long balanceAfter;
    private PointHistory.TransactionType type;

    public static UserPointResponse from(PointHistoryResult pointHistoryResult) {
        return new UserPointResponse(
                pointHistoryResult.getId(),
                pointHistoryResult.getAmount(),
                pointHistoryResult.getBalanceBefore(),
                pointHistoryResult.getBalanceAfter(),
                pointHistoryResult.getType()
        );
    }

    public static List<UserPointResponse> from(List<PointHistoryResult> resultList) {
        return resultList.stream()
                .map(UserPointResponse::from)
                .collect(Collectors.toList());
    }
}
