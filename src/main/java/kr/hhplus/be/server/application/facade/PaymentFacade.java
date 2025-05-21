package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.application.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.PopularProductService;
import kr.hhplus.be.server.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;
    private final UserPointService userPointService;
    private final PopularProductService popularProductService;
    private final OrderEventPublisher orderEventPublisher;

    public Payment processPayment(Long orderId, Long totalAmount){

        Order order = orderService.getOrder(orderId);
        Payment payment = new Payment(orderId, totalAmount);

        try{
            // 재고 차감은 주문 생성 시 진행함
            userPointService.use(order.getUserId(), totalAmount);

            payment.complete();
            order.complete();

            for (OrderItem item : order.getItems()) {
                popularProductService.incrementProductScore(item.getProductId(), item.getQuantity());
            }
        }catch (Exception e){
            String failReason = e.getMessage();
            payment.fail(failReason);
            order.fail();
        }

        return payment;
    }
}
