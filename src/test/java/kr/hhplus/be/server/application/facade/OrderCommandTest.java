package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.interfaces.api.payment.PaymentCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCommandTest {

    @InjectMocks
    private OrderCommand orderCommand;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Test
    void 주문_및_결제_생성_성공(){
        Long userId = 1L;
        Long couponId = 1L;
        Long productId = 1L;
        Long quantity = 200L;

        OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);
        List<OrderItemRequest> items = List.of(orderItemRequest);
        OrderRequest request = new OrderRequest(userId, couponId, items);

        Order mockOrder = mock(Order.class);
        when(orderService.create(request)).thenReturn(mockOrder);
        doAnswer(invocation -> null).when(paymentService).create(any(PaymentCreateRequest.class));

        // when
        Order result = orderCommand.order(request);

        // then
        verify(orderService).create(request);
        verify(paymentService).create(any(PaymentCreateRequest.class));
        verify(mockOrder).complete();
        verify(mockOrder, never()).fail();
        assertEquals(mockOrder, result);
    }

    @Test
    void 주문_및_결제_생성_실패(){
        Long userId = 1L;
        Long couponId = 1L;
        Long productId = 1L;
        Long quantity = 200L;

        OrderItemRequest orderItemRequest = new OrderItemRequest(productId, quantity);
        List<OrderItemRequest> items = List.of(orderItemRequest);
        OrderRequest request = new OrderRequest(userId, couponId, items);

        Order mockOrder = mock(Order.class);
        when(orderService.create(request)).thenReturn(mockOrder);

        doThrow(new RuntimeException("결제 생성 실패")).when(paymentService).create(any(PaymentCreateRequest.class));

        // when
        Order result = orderCommand.order(request);

        // then
        verify(orderService).create(request);
        verify(paymentService).create(any(PaymentCreateRequest.class));
        verify(mockOrder, never()).complete();
        verify(mockOrder).fail();
        assertEquals(mockOrder, result);
    }
}
