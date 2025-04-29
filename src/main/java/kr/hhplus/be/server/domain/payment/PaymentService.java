package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderErrorCode;
import kr.hhplus.be.server.domain.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment getPayment(Long paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(()-> new IllegalArgumentException(ErrorCode.PAYMENT_NOT_FOUND.getMessage()));
    }

    public List<Payment> getPayments(Payment.PaymentStatus status){
        if(status == null){
            return paymentRepository.findAllPayments();
        } else{
            return paymentRepository.findAllByStatus(status);
        }
    }

    public List<Payment> getUserPayments(Long userId, Payment.PaymentStatus status){
        if(status == null){
            List<Order> orders = orderRepository.findByUserId(userId);
            return orders.stream()
                    .map(order -> paymentRepository.findByOrderId(order.getOrderId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }else{
            List<Order> orders = orderRepository.findByUserIdAndStatus(userId, null);
            return orders.stream()
                    .map(order -> paymentRepository.findByOrderId(order.getOrderId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    public PaymentResult create(PaymentCommand command){
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorCode.ORDER_ITEM_NOT_FOUND.getMessage()));

        Long totalAmount = order.getItems()
                .stream()
                .mapToLong(OrderItem::getTotalPrice)
                .sum();
        Payment payment = new Payment(command.getOrderId(), totalAmount);
        paymentRepository.save(payment);
        return PaymentResult.of(payment);
    }

    public void updateStatusComplete(Long paymentId){
        Payment payment = getPayment(paymentId);
        payment.complete();
        paymentRepository.save(payment);
    }

    public void updateStatusFail(Long paymentId, String reason){
        Payment payment = getPayment(paymentId);
        payment.fail(reason);
        paymentRepository.save(payment);
    }
}
