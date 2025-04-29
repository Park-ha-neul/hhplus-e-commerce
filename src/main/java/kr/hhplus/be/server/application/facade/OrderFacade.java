package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public Order order(OrderFacadeRequest request){
        OrderResult orderResult = null;
        Order order = null;
        try{
            OrderCommand command = request.toCommand();
            orderResult = orderService.create(command);

            PaymentFacadeRequest paymentFacadeRequest = new PaymentFacadeRequest(orderResult.getId());
            paymentService.create(paymentFacadeRequest.toCommand());

            order = orderService.getOrder(orderResult.getId());
            order.complete();
        } catch (Exception e){
            if(orderResult != null){
                order = orderService.getOrder(orderResult.getId());
                order.fail();
            }
        }

        return order;
    }
}
