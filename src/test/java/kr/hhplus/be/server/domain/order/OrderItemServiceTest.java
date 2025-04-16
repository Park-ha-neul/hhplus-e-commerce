package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void 주문아이디로_주문상세_조회(){
        Long orderId = 1L;
        Order mockOrder = mock(Order.class);
        when(mockOrder.getOrderId()).thenReturn(orderId);

        List<OrderItem> mockItems = List.of(
                mock(OrderItem.class),
                mock(OrderItem.class)
        );

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(mockItems);

        List<OrderItem> result = orderItemService.getOrderItem(mockOrder);

        assertEquals(2, result.size());
        verify(orderItemRepository).findByOrderId(orderId);
    }

    @Test
    void 주문상세_조회시_데이터가_없는경우_예외처리(){
        Long orderId = 1L;
        Order mockOrder = mock(Order.class);
        when(mockOrder.getOrderId()).thenReturn(orderId);

        when(orderItemRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                orderItemService.getOrderItem(mockOrder)
        );

        assertEquals(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    void 주문상세_등록(){
        Order order = mock(Order.class);
        Product product = mock(Product.class);
        OrderItem orderItem = mock(OrderItem.class);

        Long quantity = 3L;
        Long unitPrice = 5000L;

        OrderItem savedItem = OrderItem.create(order, product, quantity, unitPrice);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(savedItem);

        OrderItem result = orderItemService.createOrderItem(order, product, quantity, unitPrice);

        assertNotNull(result);
        assertEquals(order, result.getOrder());
        assertEquals(product, result.getProduct());
        assertEquals(quantity, result.getQuantity());
        assertEquals(unitPrice, result.getUnitPrice());
        assertEquals(Long.valueOf(quantity * unitPrice), result.getTotalPrice());
    }
}
