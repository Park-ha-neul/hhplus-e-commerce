package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.application.facade.PaymentCompletedPopularProductEvent;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.PopularProductService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class PaymentCompletedPopularProductHandler {

    private final OrderService orderService;
    private final PopularProductService popularProductService;

    public PaymentCompletedPopularProductHandler(OrderService orderService, PopularProductService popularProductService) {
        this.orderService = orderService;
        this.popularProductService = popularProductService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedPopularProductEvent event) {
        List<OrderItem> items = orderService.getOrder(event.getOrderId()).getItems();
        for (OrderItem item : items) {
            popularProductService.incrementProductScore(item.getProductId(), item.getQuantity());
        }
    }
}
