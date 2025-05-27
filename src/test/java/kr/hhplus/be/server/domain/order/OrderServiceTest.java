package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Test
    void 주문생성_이벤트발행_확인() {
        // given
        Long userId = 1L;
        Long userCouponId = 10L;
        OrderCommand command = createTestCommand(userId, userCouponId);

        User user = new User("하늘", false);
        UserCoupon userCoupon = new UserCoupon(user.getUserId(), userCouponId, UserCoupon.UserCouponStatus.ISSUED);

        given(userRepository.findById(userId)).willReturn(user);
        given(userCouponRepository.findById(userCouponId)).willReturn(userCoupon);

        // when
        orderService.create(command);

        // then
        then(eventPublisher).should().publish(any(OrderCreatedStockDeductEvent.class), any(OrderCreatedPointUsedEvent.class),
                any(OrderCreatedCouponUsedEvent.class), any(OrderCreatedPaymentEvent.class));
    }

    private OrderCommand createTestCommand(Long userId, Long userCouponId) {
        return new OrderCommand(
                userId,
                userCouponId,
                List.of(new OrderItemCommand(1L, 2L, 1000L)) // productId, quantity, price
        );
    }

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long point = 100L;
        Long couponId = 2L;
        Long productId = 10L;
        Long quantity = 3L;
        long price = 1000L;

        OrderItemCommand itemCommand = new OrderItemCommand(productId, quantity, price);
        OrderCommand command = new OrderCommand(userId, couponId, List.of(itemCommand));

        User user = User.builder().userName("하늘").adminYn(true).build();
        UserCoupon userCoupon = UserCoupon.builder().userId(userId).couponId(couponId).build();
        Product product = Product.builder().productId(productId).price(price).quantity(100L).build();

        when(userRepository.findById(userId)).thenReturn(user);
        when(userCouponRepository.findById(couponId)).thenReturn(userCoupon);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // save stub

        // when
        OrderResult orderResult = orderService.create(command);

        // then
        assertNotNull(orderResult);
        assertEquals(Order.OrderStatus.PENDING, orderResult.getStatus());
        assertEquals(1, orderResult.getItem().size());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void SUCCESS_상태별_주문_조회() {
        // given
        Order order = mock(Order.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findByStatus(Order.OrderStatus.SUCCESS, pageable))
                .thenReturn(orderPage);

        // when
        Page<Order> result = orderService.getOrders(Order.OrderStatus.SUCCESS, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findByStatus(Order.OrderStatus.SUCCESS, pageable);
    }

    @Test
    void 전체_주문조회() {
        // given
        Order order = mock(Order.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(pageable))
                .thenReturn(orderPage);

        // when
        Page<Order> result = orderService.getOrders(null, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void 주문_단건_조회() {
        // given
        Long orderId = 1L;
        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        Order result = orderService.getOrder(orderId);

        // then
        assertEquals(order, result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void 사용자별_주문_목록_조회() {
        // given
        Long userId = 1L;
        Order order = mock(Order.class);
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));

        // when
        List<Order> result = orderService.getUserOrders(userId, null);

        // then
        assertEquals(1, result.size());
        verify(orderRepository).findByUserId(userId);
    }

    @Test
    void 주문_취소() {
        // given
        Long orderId = 1L;

        Order order = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.cancel(orderId);

        // then
        verify(order).cancel();
        verify(orderRepository).save(order);
    }
}
