package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final PaymentCompletedEventPublisher paymentCompletedEventPublisher;

    public Payment processPayment(Long orderId, Long totalAmount){

        Order order = orderService.getOrder(orderId);
        Payment payment = new Payment(orderId, totalAmount);

        try{
            payment.complete();
            order.complete();

            paymentCompletedEventPublisher.publishPaymentEvent(new PaymentCompletedEvent(
                    orderId,
                    order.getUserId(),
                    order.getItems(),
                    order.getUpdatedDate()
            ));
        }catch (Exception e){
            String failReason = e.getMessage();
            payment.fail(failReason);
            order.fail();
        }

        return payment;
    }
}
