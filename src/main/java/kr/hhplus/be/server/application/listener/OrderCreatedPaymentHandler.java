package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.domain.order.OrderCreatedEvent;
import kr.hhplus.be.server.domain.payment.PaymentPreviewCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedPaymentHandler {

    private final PaymentService paymentService;

    public OrderCreatedPaymentHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Long orderId = event.getOrderId();
        paymentService.preview(new PaymentPreviewCommand(orderId));
    }
}
