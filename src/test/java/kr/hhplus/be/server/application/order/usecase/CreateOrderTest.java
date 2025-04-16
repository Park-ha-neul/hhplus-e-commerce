package kr.hhplus.be.server.application.order.usecase;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrderTest {

    @InjectMocks
    private CreateOrder createOrder;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @Test
    void 상품_등록(){
        UserPoint userPoint = mock(UserPoint.class);
        UserCoupon userCoupon = mock(UserCoupon.class);
        Product product = mock(Product.class);
        Long quantity = 2L;
        Long price = 100L;

        Order mockOrder = mock(Order.class);
        when(orderService.create(userPoint, userCoupon)).thenReturn(mockOrder);

        OrderItemRequest request = new OrderItemRequest(mockOrder, product, quantity, price);
        List<OrderItemRequest> requests = List.of(request, request);

        when(orderItemService.createOrderItem(any(Order.class), any(Product.class), anyLong(), anyLong()))
                .thenReturn(mock(OrderItem.class));

        Order result = createOrder.execute(userPoint, userCoupon, requests);

        // Then
        assertNotNull(result);
        assertEquals(mockOrder, result);

        verify(orderService, times(1)).create(userPoint, userCoupon);
        verify(orderItemService, times(2))
                .createOrderItem(eq(mockOrder), eq(product), eq(quantity), eq(price));
    }
}
