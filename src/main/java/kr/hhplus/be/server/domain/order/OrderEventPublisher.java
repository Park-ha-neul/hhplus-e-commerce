package kr.hhplus.be.server.domain.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void publishOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        eventPublisher.publishEvent(orderCreatedEvent);
    }
}
