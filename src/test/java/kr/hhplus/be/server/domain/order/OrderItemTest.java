package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderItemTest {

    @Test
    void 상품_상세내용_저장(){
        Order dummyOrder = mock(Order.class);
        Product dummyProduct = mock(Product.class);
        Long unitPrice = 5000L;
        Long quantity = 3L;

        // when
        OrderItem orderItem = OrderItem.create(dummyOrder, dummyProduct, quantity, unitPrice);
        Long totalPrice = orderItem.getTotalPrice();

        // then
        assertEquals(Long.valueOf(15000L), totalPrice);
    }
}
