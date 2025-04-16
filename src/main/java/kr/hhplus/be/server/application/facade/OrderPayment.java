package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCreateRequest;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.TopProductService;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderPayment {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final PaymentService paymentService;
    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final UserPointService userPointService;
    private final TopProductService topProductService;

    // 주문 생성 -> 결제 -> 포인트 차감 -> 상품 상위 등록 -> 주문 완료
    public Payment processOrderAndPayment(UserPoint point, UserCoupon coupon, List<OrderItemRequest> items, PeriodType periodType) {
        Order order = orderService.create(point, coupon);

        for (OrderItemRequest item : items) {
            orderItemService.createOrderItem(order, item.getProduct(), item.getQuantity(), item.getPrice());
        }

        long discount = 0L;
        if (coupon != null) {
            discount = couponService.calculateDiscount(coupon, order);
        }

        long totalAmount = order.getOrderItems().stream()
                .mapToLong(item -> item.getTotalPrice()).sum();

        long finalAmount = totalAmount - discount;

        PaymentCreateRequest request = new PaymentCreateRequest(order, finalAmount);
        Payment payment = paymentService.create(request);

        if (point != null && finalAmount > 0) {
            userPointService.use(point.getUserId(), finalAmount);
        }

        for (OrderItem item : order.getOrderItems()) {
            topProductService.registerTopProduct(item.getProduct().getProductId(), periodType, item.getQuantity());
        }

        orderService.completeOrder(order.getOrderId());

        if (coupon != null) {
            userCouponService.useCoupon(coupon.getId());
        }

        return payment;
    }
}
