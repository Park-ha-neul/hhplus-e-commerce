package kr.hhplus.be.server.application.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessPayment {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;
    private final UserPointService userPointService;
    private final UserCouponService userCouponService;

    @Transactional
    public Payment processPayment(Long paymentId){

        Payment payment = paymentService.getPayment(paymentId);
        Order order = orderService.getOrder(payment.getOrderId());

        try{
            Long totalAmount = payment.getTotalAmount();

            long discount = 0L;
            if (order.getCouponId() != null){
                userCouponService.use(order.getCouponId());
                discount = couponService.calculateDiscount(order.getCouponId(), totalAmount);
            }
            long finalAmount = totalAmount - discount;

            userPointService.use(order.getUserId(), finalAmount);
            payment.complete();
            order.complete();
        }catch (Exception e){
            String failReason = e.getMessage();
            payment.fail(failReason);
            order.fail();
        }

        return payment;
    }
}
