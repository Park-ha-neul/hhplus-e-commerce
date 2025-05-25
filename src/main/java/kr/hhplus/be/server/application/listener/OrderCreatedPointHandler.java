package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.domain.order.OrderCreatedPointUsedEvent;
import kr.hhplus.be.server.domain.user.UserPointService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedPointHandler {

    private final UserPointService userPointService;


    public OrderCreatedPointHandler(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedPointUsedEvent event) {
        Long userId = event.getUserId();
        Long usedPoint = event.getTotalAmount();
        userPointService.use(userId, usedPoint);
    }

}
