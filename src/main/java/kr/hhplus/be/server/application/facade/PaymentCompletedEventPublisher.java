package kr.hhplus.be.server.application.facade;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public PaymentCompletedEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishPaymentEvent(PaymentCompletedEvent event){
        eventPublisher.publishEvent(event);
    }
}
