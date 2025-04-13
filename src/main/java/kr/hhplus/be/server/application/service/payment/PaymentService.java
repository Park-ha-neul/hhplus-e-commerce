package kr.hhplus.be.server.application.service.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.facade.UserPointFacadeService;
import kr.hhplus.be.server.application.service.order.OrderItemService;
import kr.hhplus.be.server.application.service.order.OrderService;
import kr.hhplus.be.server.application.service.product.TopProductService;
import kr.hhplus.be.server.application.service.userPoint.UserService;
import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCreateRequest;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.TopProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserPointFacadeService userPointFacadeService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TopProductService topProductService;

    @Autowired
    private OrderItemService orderItemService;

    @Transactional
    public Payment createPayment(PaymentCreateRequest request){
        Order order = orderService.getOrderById(request.getOrderId());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTotalAmount(request.getTotalAmount());
        payment.setPending();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processPayment(Long paymentId){
        Payment payment = paymentRepository.findByOrderId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수가 없습니다."));

        if(payment.isCompleted()){
            throw new IllegalArgumentException("이미 완료된 결제입니다.");
        }

        Long userId = payment.getOrder().getUserPoint().getUserId();
        Long amount = payment.getTotalAmount();
        userPointFacadeService.usePoint(userId, amount);

        payment.complete();

        List<OrderItem> orderItems = payment.getOrder().getOrderItems();
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            Long count = item.getQuantity();

            for (PeriodType period : PeriodType.values()) {
                LocalDate date = TopProduct.calculateCalculatedDate(period);
                topProductService.saveOrUpdateTopProduct(product.getProductId(), count, date, period);
            }
        }
        return payment;
    }
}
