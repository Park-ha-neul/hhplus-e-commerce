package kr.hhplus.be.server.integration;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.facade.OrderCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@Transactional
@DisplayName("주문 + 결제 통합 테스트")
public class OrderIntegrationTest {

    @Autowired
    private OrderCommand orderCommand;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    private Long userId;
    private Long productId;
    private Long userCouponId;
    private Long quantity;
    private Long totalStock;

    @BeforeEach
    void setUp() {
        // 사용자 등록
        User user = new User("하늘", true);
        userRepository.save(user);
        userId = user.getUserId();

        ProductRequest request = new ProductRequest("상품", "설명", 2000L, 100L);

        // 상품 등록
        Product product = Product.create(request);
        productRepository.save(product);
        productId = product.getProductId();
        totalStock = product.getQuantity();

        // 유저 쿠폰 등록
        Coupon coupon = new Coupon("10% 할인", 10L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.create(userId, coupon.getCouponId());
        userCouponRepository.save(userCoupon);
        userCouponId = userCoupon.getUserCouponId();

        UserPoint userPoint = new UserPoint(userId, 1000L);
        userPointRepository.save(userPoint);

        quantity = 20L;
    }

    @Test
    @DisplayName("정상적인 주문 요청 시, 주문 상태는 completed가 되고 결제가 생성됩니다.")
    void 주문_등록시_결제_정상_처리(){
        OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);
        List<OrderItemRequest> items = List.of(orderItemRequest);
        OrderRequest request = new OrderRequest(userId, userCouponId, items);

        Order order = orderCommand.order(request);

        assertEquals(Order.OrderStatus.SUCCESS, order.getStatus());
        Payment payment = paymentRepository.findByOrderId(order.getOrderId());
        assertNotNull(payment);
        assertEquals(order.getOrderId(), payment.getOrderId());
        Optional<Product> product = productRepository.findById(productId);
        Long expectedAmount = totalStock - quantity;
        assertEquals(expectedAmount, product.get().getQuantity());
    }

    @Test
    @DisplayName("재고 부족으로 주문이 실패합니다.")
    void 주문_재고_부족(){
        ProductRequest productRequest = new ProductRequest("상품", "설명", 2000L, 0L);
        Product product = Product.create(productRequest);
        productRepository.save(product);

        OrderItemRequest orderItemRequest = new OrderItemRequest(product.getProductId(), 10L);
        List<OrderItemRequest> items = List.of(orderItemRequest);
        OrderRequest request = new OrderRequest(userId, userCouponId, items);

        Order order = orderCommand.order(request);

        assertEquals(Order.OrderStatus.FAIL, order.getStatus());
    }
}
