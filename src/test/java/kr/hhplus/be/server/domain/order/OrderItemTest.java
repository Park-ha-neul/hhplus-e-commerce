package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OrderItemTest {

    @Test
    void 총_주문_상품_가격_조회(){
        Long userId = 1L;
        Long couponId = 2L;
        Order order = new Order(userId, couponId);

        Long productId = 1L;
        Long quantity = 10L;
        Long price = 2000L;
        OrderItem orderItem = new OrderItem(order, productId, quantity, price);

        Long result = orderItem.getTotalPrice();

        assertEquals(Long.valueOf(quantity * price), result);
    }
}
