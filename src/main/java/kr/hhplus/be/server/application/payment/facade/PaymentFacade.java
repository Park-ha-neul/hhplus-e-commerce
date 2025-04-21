package kr.hhplus.be.server.application.payment.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;

    @Transactional
    public Payment processPayment(Long paymentId){

        Payment payment = paymentService.getPayment(paymentId);
        Order order = orderService.getOrder(payment.getOrderId());

        try{
            Long totalAmount = order.getItems().stream()
                    .mapToLong(OrderItem::getTotalPrice)
                    .sum();

            Coupon userCoupon = couponService.getCoupon(order.getCouponId());

            long discount = 0L;
            if (userCoupon != null){
                couponService.use(order.getCouponId());
                discount = userCoupon.calculateDiscount(totalAmount);
            }
            long finalAmount = totalAmount - discount;

            UserWithPointResponse user = userService.getUser(order.getUserId());

            // 포인트 부족하면 예외 발생
            user.getUserPoint().use(finalAmount);
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
