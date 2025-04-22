package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
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
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long point = 100L;
        Long couponId = 2L;
        Long productId = 10L;
        Long quantity = 3L;
        long price = 1000L;

        OrderItemRequest itemRequest = new OrderItemRequest(productId, quantity);
        OrderRequest request = new OrderRequest(userId, couponId, List.of(itemRequest));

        User user = User.builder().userName("하늘").adminYn(true).build();
        UserCoupon userCoupon = UserCoupon.builder().userId(userId).couponId(couponId).build();
        Product product = Product.builder().productId(productId).price(price).build();

        when(userRepository.findById(userId)).thenReturn(user);
        when(userCouponRepository.findById(couponId)).thenReturn(Optional.of(userCoupon));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0)); // save stub

        // when
        Order order = orderService.create(request);

        // then
        assertNotNull(order);
        assertEquals(Order.OrderStatus.SUCCESS, order.getStatus());
        assertEquals(1, order.getItems().size());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void 상태별_주문_조회() {
        // given
        Order order = mock(Order.class);
        when(orderRepository.findByStatus(Order.OrderStatus.SUCCESS))
                .thenReturn(List.of(order));

        // when
        List<Order> result = orderService.getOrders(Order.OrderStatus.SUCCESS);

        // then
        assertEquals(1, result.size());
        verify(orderRepository).findByStatus(Order.OrderStatus.SUCCESS);
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
        List<Order> result = orderService.getOrderByUserId(userId);

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
