package kr.hhplus.be.server.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Transactional
@DisplayName("결제 + 포인트 차감 + 쿠폰 사용 완료 통합 테스트")
public class PaymentIntegrationTest {

    @Autowired
    private PaymentFacade processPayment;

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

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Long paymentId;
    private Long orderId;
    private Long couponId;
    private Long userId;
    private Long productId;
    private Long productId2;
    private Long initPoint;
    private Long userCouponId;
    private Long discount;
    private Long totalAmount;

    private final Logger logger = LoggerFactory.getLogger(PaymentIntegrationTest.class);

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

        ProductCommand command = new ProductCommand("상품", "설명", 2000L, 100L);
        ProductCommand command2 = new ProductCommand("상품2", "설명", 2000L, 100L);

        // 상품 등록
        Product product = Product.create(command);
        productRepository.save(product);
        productId = product.getProductId();
        Product product2 = Product.create(command2);
        productRepository.save(product2);
        productId2 = product2.getProductId();

        // 4. 쿠폰 등록
        Coupon coupon = new Coupon("10% 할인", 10L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        couponRepository.save(coupon);
        couponId = coupon.getCouponId();

        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);
        userCouponId = userCoupon.getUserCouponId();

        Order order = new Order(userId, userCouponId);
        OrderItem orderItem = new OrderItem(order, productId, 200L, 1000L);
        OrderItem orderItem2 = new OrderItem(order, productId2, 300L, 1000L);
        order.addOrderItem(orderItem);
        order.addOrderItem(orderItem2);
        orderRepository.save(order);
        orderId = order.getOrderId();
        logger.info("Saved order id: {}", orderId);
        logger.info("Saved order items: {}", order.getItems().size());
        for (OrderItem item : order.getItems()) {
            logger.info("Product ID: {}, Quantity: {}", item.getProductId(), item.getQuantity());
        }

        // 5. 결제 생성
        totalAmount = 200L;
        Payment payment = new Payment(orderId, totalAmount); // 결제 금액
        paymentRepository.save(payment);
        paymentId = payment.getPaymentId();
        discount = coupon.calculateDiscount(totalAmount);
    }

    @AfterEach
    void cleanRedis() {
        String redisKey = "popular:products:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisTemplate.delete(redisKey);
    }

    @Test
    @DisplayName("결제 후 Redis ZSet 점수 및 TTL 확인 (디버깅 포함)")
    void 결제_후_redis_디버깅_전체() {
        Payment payment = processPayment.processPayment(orderId, totalAmount);

        String redisKey = "popular:products:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        logger.info("Redis Key: {}", redisKey);

        // Key 존재 확인
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        logger.info("Key 존재 여부: {}", hasKey);

        // ZSet 값 확인
        Set<ZSetOperations.TypedTuple<Object>> zsetValues =
                redisTemplate.opsForZSet().rangeWithScores(redisKey, 0, -1);

        if (zsetValues == null || zsetValues.isEmpty()) {
            logger.warn("ZSet {} 에 값 없음", redisKey);
        } else {
            for (ZSetOperations.TypedTuple<Object> tuple : zsetValues) {
                logger.info("멤버: {}, 점수: {}", tuple.getValue(), tuple.getScore());
            }
        }

        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        logger.info("TTL (초): {}", ttl);
    }
}
