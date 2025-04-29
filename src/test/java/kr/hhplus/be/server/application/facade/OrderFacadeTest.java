package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.OrderResult;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;

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
        Long orderId = 123L;

        OrderItemFacadeRequest orderItemFacadeRequest = new OrderItemFacadeRequest(productId, quantity);
        List<OrderItemFacadeRequest> facadeItems = List.of(orderItemFacadeRequest);
        OrderFacadeRequest orderFacadeRequest = new OrderFacadeRequest(userId, couponId, facadeItems);

        Order mockOrder = mock(Order.class);
        OrderResult mockOrderResult = mock(OrderResult.class);

        when(orderService.create(any(OrderCommand.class))).thenReturn(mockOrderResult);
        when(mockOrderResult.getId()).thenReturn(orderId);
        when(orderService.getOrder(orderId)).thenReturn(mockOrder);

        doAnswer(invocation -> null).when(paymentService).create(any(PaymentCommand.class));

        // when
        Order result = orderFacade.order(orderFacadeRequest);

        // then
        verify(orderService).create(any(OrderCommand.class));
        verify(paymentService).create(any(PaymentCommand.class));
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
        Long orderId = 123L;

        OrderItemFacadeRequest orderItemFacadeRequest = new OrderItemFacadeRequest(productId, quantity);
        List<OrderItemFacadeRequest> facadeItems = List.of(orderItemFacadeRequest);
        OrderFacadeRequest orderFacadeRequest = new OrderFacadeRequest(userId, couponId, facadeItems);

        Order mockOrder = mock(Order.class);
        OrderResult mockOrderResult = mock(OrderResult.class);

        when(orderService.create(any(OrderCommand.class))).thenReturn(mockOrderResult);
        when(mockOrderResult.getId()).thenReturn(orderId);
        when(orderService.getOrder(orderId)).thenReturn(mockOrder);

        doThrow(new RuntimeException("결제 생성 실패")).when(paymentService).create(any(PaymentCommand.class));

        // when
        Order result = orderFacade.order(orderFacadeRequest);

        // then
        verify(orderService).create(any(OrderCommand.class));
        verify(paymentService).create(any(PaymentCommand.class));
        verify(mockOrder, never()).complete();
        verify(mockOrder).fail();
        assertEquals(mockOrder, result);
    }
}
