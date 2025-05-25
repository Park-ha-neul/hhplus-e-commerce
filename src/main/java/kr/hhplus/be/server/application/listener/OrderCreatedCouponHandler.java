package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.OrderCreatedCouponUsedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedCouponHandler {

    private final UserCouponService userCouponService;


    public OrderCreatedCouponHandler(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedCouponUsedEvent event) {
        Long couponId = event.getCouponId();
        userCouponService.use(couponId);
    }
}
