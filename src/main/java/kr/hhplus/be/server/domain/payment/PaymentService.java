package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderEntityRepository;
import kr.hhplus.be.server.domain.order.OrderErrorCode;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.interfaces.api.payment.PaymentCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private PaymentEntityRepository paymentEntityRepository;
    private OrderEntityRepository orderEntityRepository;

    public PaymentEntity getPayment(Long paymentId){
        return paymentEntityRepository.findById(paymentId)
                .orElseThrow(()-> new IllegalArgumentException(ErrorCode.PAYMENT_NOT_FOUND.getMessage()));
    }

    public List<PaymentEntity> getPayments(PaymentStatus status){
        List<PaymentEntity> result = paymentEntityRepository.findAllByStatus(status);
        return result;
    }

    public List<PaymentEntity> getPaymentByUserId(Long userId){
        List<PaymentEntity> result = paymentEntityRepository.findByUserId(userId);
        return result;
    }

    public PaymentEntity create(PaymentCreateRequest request){
        OrderEntity orderEntity = orderEntityRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorCode.ORDER_ITEM_NOT_FOUND.getMessage()));

        Long totalAmount = orderEntity.getOrderItemEntities()
                .stream()
                .mapToLong(OrderItemEntity::getTotalPrice)
                .sum();
        PaymentEntity paymentEntity = PaymentEntity.create(orderEntity, totalAmount);
        return paymentEntityRepository.save(paymentEntity);
    }

    public void updateStatusComplete(Long paymentId){
        PaymentEntity paymentEntity = getPayment(paymentId);
        paymentEntity.complete();
        paymentEntityRepository.save(paymentEntity);
    }

    public void updateStatusFail(Long paymentId, String reason){
        PaymentEntity paymentEntity = getPayment(paymentId);
        paymentEntity.fail(reason);
        paymentEntityRepository.save(paymentEntity);
    }
}
