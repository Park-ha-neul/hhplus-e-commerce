package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    @Test
    void 주문_성공_상태_변경(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);

        //when
        order.complete();

        // then
        assertEquals(Order.OrderStatus.SUCCESS, order.getStatus());
    }

    @Test
    void 주문_실패_상태_변경(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);

        //when
        order.fail();

        // then
        assertEquals(Order.OrderStatus.FAIL, order.getStatus());

    }

    @Test
    void 주문_취소_상태_변경(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);

        //when
        order.cancel();

        // then
        assertEquals(Order.OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void 주문에_아이템_추가(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);
        Long productId = 1L;
        Long quantity = 10L;
        Long price = 2000L;
        OrderItem orderItem = new OrderItem(order, productId, quantity, price);

        // when
        order.addOrderItem(orderItem);

        // then
        assertEquals(1, order.getItems().size()); // 아이템이 제대로 추가되었는지 확인
        assertTrue(order.getItems().contains(orderItem));
    }

    @Test
    void 주문_총_금액_계산(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);
        Long productId = 1L;
        Long quantity = 10L;
        Long price = 2000L;
        OrderItem orderItem = new OrderItem(order, productId, quantity, price);

        // when
        order.addOrderItem(orderItem);
        Long totalAmount = order.calculateTotalPrice();

        // then
        assertEquals(Long.valueOf(20000L), totalAmount);
    }
}
