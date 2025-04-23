package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.interfaces.api.payment.PaymentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCommand {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @Transactional
    public Order order(OrderRequest request){
        Order order = new Order();
        try{
            order = orderService.create(request);
            PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest(order.getOrderId());
            paymentService.create(paymentCreateRequest);
            order.complete();
        } catch (Exception e){
            order.fail();
        }
        return order;
    }
}
