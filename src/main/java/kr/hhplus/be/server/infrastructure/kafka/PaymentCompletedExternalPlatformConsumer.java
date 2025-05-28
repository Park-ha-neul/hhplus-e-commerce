package kr.hhplus.be.server.infrastructure.kafka;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.infrastructure.sender.DataPlatformSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedExternalPlatformConsumer {

    private final DataPlatformSender dataPlatformSender;
    private final Logger log = LoggerFactory.getLogger(PaymentCompletedExternalPlatformConsumer.class);

    public PaymentCompletedExternalPlatformConsumer(DataPlatformSender dataPlatformSender, OrderService orderService){
        this.dataPlatformSender = dataPlatformSender;
    }

    @KafkaListener(topics = "outside.payment.completed.external", groupId = "payment-external-platform")
    public void handleKafkaMessage(PaymentCompletedExternalPlatformMessage message){
        try{
            log.info("📥 Kafka 수신: {}", message.getOrderId());
            dataPlatformSender.sendOrder(message);
        } catch (Exception e){
            log.error("데이터 전송 실패 : {}", e.getMessage());
            throw new IllegalArgumentException("데이터 전송 실패 : " + e.getMessage());
        }
    }
}
