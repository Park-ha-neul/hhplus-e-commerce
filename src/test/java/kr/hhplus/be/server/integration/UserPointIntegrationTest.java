package kr.hhplus.be.server.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("포인트 기능 통합 테스트")
public class UserPointIntegrationTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    private Long userId;
    private Long initPoint;

    @BeforeEach
    void setUp() {
        userId = 1L;
        initPoint = 200L;
        UserPoint userPoint = new UserPoint(userId, initPoint);
        userPointRepository.save(userPoint);
    }

    @Test
    @DisplayName("사용자가 포인트를 충전하면 잔액과 충전 이력이 기록된다.")
    void 사용자_포인트_충전_이력_확인(){
        Long amount = 200L;

        userPointService.charge(userId, amount);

        List<PointHistory> histories = pointHistoryRepository.findByUserId(userId);
        assertThat(histories).hasSize(1);

        PointHistory history = histories.get(0);
        assertEquals(amount, history.getAmount());
        assertEquals(initPoint, history.getBalanceBefore());
        Long totalAmount = initPoint + amount;
        assertEquals(totalAmount, history.getBalanceAfter());
        assertEquals(PointHistory.TransactionType.CHARGE, history.getType());
    }

    @Test
    void 사용자_포인트_사용후_이력_확인(){
        Long amount = 200L;

        userPointService.use(userId, amount);

        List<PointHistory> histories = pointHistoryRepository.findByUserId(userId);
        assertThat(histories).hasSize(1);

        PointHistory history = histories.get(0);
        assertEquals(amount, history.getAmount());
        assertEquals(initPoint, history.getBalanceBefore());
        Long totalAmount = initPoint - amount;
        assertEquals(totalAmount, history.getBalanceAfter());
        assertEquals(PointHistory.TransactionType.USE, history.getType());

    }
}
