package kr.hhplus.be.server.domain.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void publish(Object... events){
        for(Object event : events){
            eventPublisher.publishEvent(event);
        }
    }
}
