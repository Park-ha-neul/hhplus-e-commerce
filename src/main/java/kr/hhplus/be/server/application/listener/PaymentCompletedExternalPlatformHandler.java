package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.application.facade.PaymentCompletedExternalPlatformEvent;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.infrastructure.sender.DataPlatformSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentCompletedExternalPlatformHandler {

    private final DataPlatformSender dataPlatformSender;
    private final Logger log = LoggerFactory.getLogger(PaymentCompletedExternalPlatformHandler.class);

    public PaymentCompletedExternalPlatformHandler(DataPlatformSender dataPlatformSender, OrderService orderService){
        this.dataPlatformSender = dataPlatformSender;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(PaymentCompletedExternalPlatformEvent event){
        try{
            dataPlatformSender.sendOrder(event);
        } catch (Exception e){
            log.error("데이터 전송 실패 : {}", e.getMessage());
            throw new IllegalArgumentException("데이터 전송 실패 : " + e.getMessage());
        }
    }
}
