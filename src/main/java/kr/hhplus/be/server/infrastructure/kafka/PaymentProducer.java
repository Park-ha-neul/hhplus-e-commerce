package kr.hhplus.be.server.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentCompletedExternalPlatformMessage> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, PaymentCompletedExternalPlatformMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PaymentCompletedExternalPlatformMessage message) {
        kafkaTemplate.send("outside.payment.completed.external", message);
    }
}
