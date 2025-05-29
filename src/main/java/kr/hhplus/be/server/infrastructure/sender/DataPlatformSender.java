package kr.hhplus.be.server.infrastructure.sender;

//import kr.hhplus.be.server.application.facade.PaymentCompletedExternalPlatformEvent;
import kr.hhplus.be.server.infrastructure.kafka.PaymentCompletedExternalPlatformMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DataPlatformSender {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrder(PaymentCompletedExternalPlatformMessage message){
        String apiUrl = "http://localhost:8080/mock/order";

        Map<String, Object> payload = Map.of(
                "orderId", message.getOrderId(),
                "userId", message.getUserId(),
                "orderItems", message.getOrderItems(),
                "orderedAt", message.getUpdatedDate().toString()
        );

        restTemplate.postForEntity(apiUrl, payload, Void.class);
    }
}
