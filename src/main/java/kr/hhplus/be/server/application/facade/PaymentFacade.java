package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.infrastructure.kafka.OrderItemDto;
import kr.hhplus.be.server.infrastructure.kafka.PaymentCompletedExternalPlatformMessage;
import kr.hhplus.be.server.infrastructure.kafka.PaymentCompletedProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final PaymentCompletedEventPublisher eventPublisher;
    private final PaymentCompletedProducer kafkaProducer;

    public Payment processPayment(Long orderId, Long totalAmount){

        Order order = orderService.getOrder(orderId);
        Payment payment = new Payment(orderId, totalAmount);

        try{
            payment.complete();
            order.complete();

            publishPaymentCompletedEvent(order);
        }catch (Exception e){
            String failReason = e.getMessage();
            payment.fail(failReason);
            order.fail();
        }

        return payment;
    }

    private void publishPaymentCompletedEvent(Order order){
        eventPublisher.publish(
                new PaymentCompletedPopularProductEvent(order.getOrderId())
        );

        PaymentCompletedExternalPlatformMessage message = new PaymentCompletedExternalPlatformMessage(
                order.getOrderId(),
                order.getUserId(),
                order.getItems().stream()
                        .map(item -> new OrderItemDto(item.getProductId(), item.getQuantity(),item.getUnitPrice()))
                        .collect(Collectors.toList()),
                LocalDateTime.now()
        );
        kafkaProducer.send(message);
    }
}
