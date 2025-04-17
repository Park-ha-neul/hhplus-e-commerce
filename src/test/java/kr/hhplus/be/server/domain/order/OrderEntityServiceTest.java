package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponEntityRepository;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductEntityRepository;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderReqeust;
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
public class OrderEntityServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @Mock
    private UserCouponEntityRepository userCouponEntityRepository;

    @Mock
    private ProductEntityRepository productEntityRepository;

    @Mock
    private OrderEntityRepository orderEntityRepository;

    @Test
    void 주문_생성_성공() {
        // given
        Long userId = 1L;
        Long couponId = 2L;
        Long productId = 10L;
        Long quantity = 3L;
        long price = 1000L;

        OrderItemRequest itemRequest = new OrderItemRequest(productId, quantity);
        OrderReqeust request = new OrderReqeust(userId, couponId, List.of(itemRequest));

        UserPointEntity userPointEntity = UserPointEntity.builder().userId(userId).build();
        UserCouponEntity userCouponEntity = UserCouponEntity.builder().userCouponId(couponId).build();
        ProductEntity productEntity = ProductEntity.builder().productId(productId).price(price).build();

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(userPointEntity));
        when(userCouponEntityRepository.findByUserAndCoupon(userId, couponId)).thenReturn(userCouponEntity);
        when(productEntityRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        // when
        OrderEntity orderEntity = orderService.createOrder(request);

        // then
        assertNotNull(orderEntity);
        assertEquals(OrderStatus.SUCCESS, orderEntity.getType());
        assertEquals(1, orderEntity.getOrderItemEntities().size());
        verify(orderEntityRepository).save(any(OrderEntity.class));
    }

    @Test
    void 상태별_주문_조회() {
        // given
        OrderEntity order = mock(OrderEntity.class);
        when(orderEntityRepository.findByStatus(OrderStatus.SUCCESS))
                .thenReturn(List.of(order));

        // when
        List<OrderEntity> result = orderService.getOrders(OrderStatus.SUCCESS);

        // then
        assertEquals(1, result.size());
        verify(orderEntityRepository).findByStatus(OrderStatus.SUCCESS);
    }

    @Test
    void 주문_단건_조회() {
        // given
        Long orderId = 1L;
        OrderEntity order = mock(OrderEntity.class);
        when(orderEntityRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        OrderEntity result = orderService.getOrder(orderId);

        // then
        assertEquals(order, result);
        verify(orderEntityRepository).findById(orderId);
    }

    @Test
    void 사용자별_주문_목록_조회() {
        // given
        Long userId = 1L;
        OrderEntity order = mock(OrderEntity.class);
        when(orderEntityRepository.findByUserId(userId)).thenReturn(List.of(order));

        // when
        List<OrderEntity> result = orderService.getOrderByUserId(userId);

        // then
        assertEquals(1, result.size());
        verify(orderEntityRepository).findByUserId(userId);
    }

    @Test
    void 주문_취소() {
        // given
        Long orderId = 1L;
        OrderEntity order = mock(OrderEntity.class);
        when(orderEntityRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        orderService.cancleOrder(orderId);

        // then
        verify(order).cancel();
        verify(orderEntityRepository).save(order);
    }
}
