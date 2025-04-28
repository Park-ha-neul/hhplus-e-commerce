package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.order.OrderResult;
import kr.hhplus.be.server.domain.payment.PaymentService;
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

        OrderItemCommand orderItemCommand = new OrderItemCommand(productId, quantity);
        List<OrderItemCommand> items = List.of(orderItemCommand);
        OrderCommand command = new OrderCommand(userId, couponId, items);

        OrderItemFacadeRequest orderItemFacadeRequest = new OrderItemFacadeRequest(productId, quantity);
        List<OrderItemFacadeRequest> facadeItems = List.of(orderItemFacadeRequest);
        OrderFacadeRequest orderFacadeRequest = new OrderFacadeRequest(userId, couponId, facadeItems);

        Order mockOrder = mock(Order.class);
        OrderResult mockOrderResult = mock(OrderResult.class);
        when(orderService.create(command)).thenReturn(mockOrderResult);
        doAnswer(invocation -> null).when(paymentService).create(any(PaymentCreateRequest.class));

        // when
        Order result = orderFacade.order(orderFacadeRequest);

        // then
        verify(orderService).create(command);
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

        OrderItemCommand orderItemCommand = new OrderItemCommand(productId, quantity);
        List<OrderItemCommand> items = List.of(orderItemCommand);
        OrderCommand command = new OrderCommand(userId, couponId, items);

        OrderItemFacadeRequest orderItemFacadeRequest = new OrderItemFacadeRequest(productId, quantity);
        List<OrderItemFacadeRequest> facadeItems = List.of(orderItemFacadeRequest);
        OrderFacadeRequest orderFacadeRequest = new OrderFacadeRequest(userId, couponId, facadeItems);

        Order mockOrder = mock(Order.class);
        OrderResult mockOrderResult = mock(OrderResult.class);
        when(orderService.create(command)).thenReturn(mockOrderResult);

        doThrow(new RuntimeException("결제 생성 실패")).when(paymentService).create(any(PaymentCreateRequest.class));

        // when
        Order result = orderFacade.order(orderFacadeRequest);

        // then
        verify(orderService).create(command);
        verify(paymentService).create(any(PaymentCreateRequest.class));
        verify(mockOrder, never()).complete();
        verify(mockOrder).fail();
        assertEquals(mockOrder, result);
    }
}
