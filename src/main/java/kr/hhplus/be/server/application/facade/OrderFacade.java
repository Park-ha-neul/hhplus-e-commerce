package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.interfaces.api.payment.PaymentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public Order order(OrderFacadeRequest request){
        Order order = new Order();
        try{
            OrderCommand command = request.toCommand();
            OrderResult orderResult = orderService.create(command);
            PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest(order.getOrderId());
            paymentService.create(paymentCreateRequest);
            order.complete();
        } catch (Exception e){
            order.fail();
        }
        return order;
    }
}
