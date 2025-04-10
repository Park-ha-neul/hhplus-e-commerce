package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.application.service.order.OrderItemService;
import kr.hhplus.be.server.application.service.order.OrderService;
import kr.hhplus.be.server.application.service.userPoint.UserService;
import kr.hhplus.be.server.domain.common.OrderType;
import kr.hhplus.be.server.domain.common.ProductStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDto;
import kr.hhplus.be.server.domain.order.OrderItemDto;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserOrderFacadeServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private UserOrderFacadeService userOrderFacadeService;

    @Test
    public void 주문_등록_성공(){
        Long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 1000L, false);
        OrderDto orderDto = new OrderDto(userId, new ArrayList<>());

        // 주문 항목 추가
        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;
        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderDto.getOrderItems().add(orderItemDto);

        when(userService.getUser(orderDto.getUserId())).thenReturn(userPoint);

        Order order = new Order();
        order.setOrderId(1L);
        order.setUserPoint(userPoint);  // 유저 포인트 설정
        order.setType(OrderType.SUCCESS);  // 초기 상태 설정
        when(orderService.createOrder(orderDto)).thenReturn(order);

        doNothing().when(orderService).updateOrderStatus(order.getOrderId(), OrderType.SUCCESS);

        // when
        Order createdOrder = userOrderFacadeService.createOrderWithItems(orderDto);

        // then
        assertNotNull(createdOrder); // createdOrder가 null이 아님을 확인
        assertEquals(userPoint, createdOrder.getUserPoint());
        assertEquals(OrderType.SUCCESS, createdOrder.getType());
        verify(orderService, times(1)).updateOrderStatus(createdOrder.getOrderId(), OrderType.SUCCESS);
    }

    @Test
    public void 주문_등록_실패_유효하지_않은_사용자_예외처리(){
        Long userId = 1L;
        OrderDto orderDto = new OrderDto(userId, new ArrayList<>());

        // 주문 항목 추가
        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;
        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderDto.getOrderItems().add(orderItemDto);

        when(userService.getUser(orderDto.getUserId())).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 조회 시 IllegalArgumentException 발생해야 함
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userOrderFacadeService.createOrderWithItems(orderDto);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 주문_등록_실패_재고가_부족한_경우_예외처리() {
        Long userId = 1L;
        OrderDto orderDto = new OrderDto(userId, new ArrayList<>());

        // 주문 항목 추가
        Long productId = 1L;
        Long quantity = 3L;  // 재고보다 많은 양
        Long unitPrice = 100L;
        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderDto.getOrderItems().add(orderItemDto);

        // 재고가 부족한 상품을 준비 (재고 2개)
        Product product = new Product(1L, "A 상품", "설명", 2000L, new Balance(2L), ProductStatus.AVAILABLE);
        when(userService.getUser(orderDto.getUserId())).thenReturn(new UserPoint(userId, 1000L, false));
        when(orderService.createOrder(orderDto)).thenReturn(new Order()); // 주문 생성 후 주문 ID 반환

        doThrow(new IllegalArgumentException("재고가 부족합니다."))
                .when(orderItemService).createOrderItem(any(OrderItemDto.class), any(Order.class));

        // 예외 발생 검증
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userOrderFacadeService.createOrderWithItems(orderDto);
        });

        // 예외 메시지 검증
        assertEquals("재고가 부족합니다.", e.getMessage());
    }
}
