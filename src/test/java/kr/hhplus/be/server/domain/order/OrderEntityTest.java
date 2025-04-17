package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class OrderEntityTest {

    @Test
    void 주문_등록_성공(){
        UserPointEntity userPointEntity = UserPointEntity.builder().userId(1L).build();
        UserCouponEntity coupon = UserCouponEntity.builder().userCouponId(1L).build();

        // when
        OrderEntity order = OrderEntity.create(userPointEntity, coupon);

        // then
        assertNotNull(order);
        assertEquals(OrderStatus.PENDING, order.getType());
        assertEquals(userPointEntity, order.getUserPointEntity());
        assertEquals(coupon, order.getUserCouponEntity());
    }

    @Test
    void 주문_완료(){
        UserPointEntity userPointEntity = UserPointEntity.builder().userId(1L).build();
        UserCouponEntity coupon = UserCouponEntity.builder().userCouponId(1L).build();
        OrderEntity order = OrderEntity.create(userPointEntity, coupon);

        // when
        order.complete();

        // then
        assertEquals(OrderStatus.SUCCESS, order.getType());
    }

    @Test
    void 주문_취소(){
        UserPointEntity userPointEntity = UserPointEntity.builder().userId(1L).build();
        UserCouponEntity coupon = UserCouponEntity.builder().userCouponId(1L).build();
        OrderEntity order = OrderEntity.create(userPointEntity, coupon);

        // when
        order.cancel();

        // then
        assertEquals(OrderStatus.FAIL, order.getType());
    }

    @Test
    void 주문에_아이템_추가(){
        UserPointEntity userPointEntity = UserPointEntity.builder().userId(1L).build();
        UserCouponEntity coupon = UserCouponEntity.builder().userCouponId(1L).build();
        OrderEntity order = OrderEntity.create(userPointEntity, coupon);

        OrderItemEntity orderItem = OrderItemEntity.builder().orderItemId(1L).build();

        // when
        order.addOrderItem(orderItem);

        // then
        assertEquals(1, order.getOrderItemEntities().size()); // 아이템이 제대로 추가되었는지 확인
        assertTrue(order.getOrderItemEntities().contains(orderItem));
    }
}
