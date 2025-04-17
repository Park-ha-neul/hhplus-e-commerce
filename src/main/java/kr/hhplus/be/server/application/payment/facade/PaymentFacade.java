package kr.hhplus.be.server.application.payment.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.domain.payment.ErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentEntityRepository;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentEntityRepository paymentEntityRepository;

    @Transactional
    public PaymentEntity completePayment(Long paymentId){
        PaymentEntity paymentEntity = paymentEntityRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(ErrorCode.PAYMENT_NOT_FOUND.getMessage())));

        OrderEntity orderEntity = paymentEntity.getOrderEntity();

        Long totalAmount = orderEntity.getOrderItemEntities().stream()
                .mapToLong(OrderItemEntity::getTotalPrice)
                .sum();

        UserCouponEntity couponEntity = orderEntity.getUserCouponEntity();
        long discount = 0L;
        if (couponEntity != null){
            discount = couponEntity.getCouponEntity().calculateDiscount(totalAmount);
            couponEntity.use();
        }
        long finalAmount = totalAmount - discount;

        UserPointEntity userPointEntity = orderEntity.getUserPointEntity();
        userPointEntity.getPoint().use(finalAmount);

        paymentEntity.complete();
        orderEntity.complete();

        return paymentEntityRepository.save(paymentEntity);
    }
}
