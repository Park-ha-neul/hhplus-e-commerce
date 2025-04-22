package kr.hhplus.be.server.application.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.facade.ProcessPayment;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@DisplayName("결제 + 포인트 차감 + 쿠폰 사용 완료 통합 테스트")
public class PaymentIntegrationTest {

    @Autowired
    private ProcessPayment processPayment;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponService couponService;

    private Long paymentId;
    private Long orderId;
    private Long couponId;
    private Long userId;
    private Long productId;
    private Long initPoint;
    private Long userCouponId;
    private Long discount;
    private Long totalAmount;

    @BeforeEach
    void setUp() {
        // 1. 사용자 등록
        User user = new User("하늘", true);
        userRepository.save(user);
        userId = user.getUserId();

        // 2. 포인트 등록
        UserPoint userPoint = new UserPoint(userId, 1000L); // 1000 포인트
        userPointRepository.save(userPoint);
        initPoint = userPoint.getPoint();
        System.out.println("initPoint : " + initPoint);

        ProductRequest request = new ProductRequest("상품", "설명", 2000L, 100L);

        // 상품 등록
        Product product = Product.create(request);
        productRepository.save(product);
        productId = product.getProductId();

        // 4. 쿠폰 등록
        Coupon coupon = new Coupon("10% 할인", 10L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        couponRepository.save(coupon);
        couponId = coupon.getCouponId();

        UserCoupon userCoupon = new UserCoupon(userId, couponId);
        userCouponRepository.save(userCoupon);
        userCouponId = userCoupon.getUserCouponId();

        Order order = new Order(userId, userCouponId);
        orderRepository.save(order);
        orderId = order.getOrderId();

        // 5. 결제 생성
        totalAmount = 200L;
        Payment payment = new Payment(orderId, totalAmount); // 결제 금액
        paymentRepository.save(payment);
        paymentId = payment.getPaymentId();
        discount = coupon.calculateDiscount(totalAmount);
    }


    @Test
    @DisplayName("정상적으로 결제 완료 시, 포인트가 차감되며 쿠폰은 사용됨으로 변경됩니다.")
    void 결제_완료시_포인트_차감및_쿠폰_사용(){
        System.out.println("given paymentId : " + paymentId);
        Payment payment = processPayment.processPayment(paymentId);

        assertEquals(Payment.PaymentStatus.COMPLETED, payment.getStatus());
        Long discountAmount = totalAmount - discount;
        Long expectedAmount = initPoint - discountAmount;
        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(expectedAmount, userPoint.getPoint());
    }
}
