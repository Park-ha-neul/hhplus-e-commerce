package kr.hhplus.be.server.application.service.order;

import kr.hhplus.be.server.domain.common.ProductStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItemDto;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    public void 주문_상세_등록(){
        // given
        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;

        Product product = new Product();
        product.setProductId(productId);
        product.setName("A 상품");
        product.setDescription("설명");
        product.setPrice(200L);
        Balance balance = new Balance(10L);
        product.setBalance(balance);

        Order order = new Order();
        order.setOrderId(1L);

        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);
        orderItemDto.setProductId(productId);
        orderItemDto.setQuantity(quantity);
        orderItemDto.setUnitPrice(unitPrice);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        orderItemService.createOrderItem(orderItemDto, order);

        assertEquals(Long.valueOf(8L), product.getBalance().getQuantity());
    }

    @Test
    public void 없는상품_주문시_주문상세_등록_예외처리(){
        // given
        Long productId = 1L;
        Long quantity = 2L;
        Long unitPrice = 100L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Order order = new Order();
        order.setOrderId(1L);

        OrderItemDto orderItemDto = new OrderItemDto(productId, quantity, unitPrice);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            orderItemService.createOrderItem(orderItemDto, order);
        });

        assertEquals("상품이 존재하지 않습니다.", e.getMessage());
    }
}
