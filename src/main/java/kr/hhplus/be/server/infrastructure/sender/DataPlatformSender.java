package kr.hhplus.be.server.infrastructure.sender;

import kr.hhplus.be.server.application.facade.PaymentCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DataPlatformSender {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOrder(PaymentCompletedEvent event){
        String apiUrl = "http://localhost:8080/mock/order";

        Map<String, Object> payload = Map.of(
                "orderId", event.getOrderId(),
                "userId", event.getUserId(),
                "orderItems", event.getOrderItems(),
                "orderedAt", event.getUpdatedDate().toString()
        );

        restTemplate.postForEntity(apiUrl, payload, Void.class);
    }
}
