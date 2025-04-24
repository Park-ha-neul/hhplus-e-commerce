package kr.hhplus.be.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.facade.OrderCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderCommand orderCommand;

    @Test
    void 주문_등록_성공() throws Exception {
        OrderItemRequest orderItem1  = new OrderItemRequest();
        OrderItemRequest orderItem2  = new OrderItemRequest();
        List<OrderItemRequest> items = List.of(orderItem1, orderItem2);

        OrderRequest request = new OrderRequest(1L, 1L, items);
        Order mockOrder = new Order();

        given(orderCommand.order(any(OrderRequest.class))).willReturn(mockOrder);

        mockMvc.perform(post("/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    void 주문_목록_조회_성공() throws Exception {
        List<Order> orders = List.of(
                new Order(1L, 1L),
                new Order(2L, 2L)
        );

        given(orderService.getOrders(null)).willReturn(orders);

        mockMvc.perform(get("/orders/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void 주문_상세_조회_성공() throws Exception {
        Long orderId = 1L;
        Order mockOrder = new Order(1L, 1L);

        given(orderService.getOrder(orderId)).willReturn(mockOrder);

        mockMvc.perform(get("/orders/{order_id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    void 주문_취소_성공() throws Exception {
        Long orderId = 1L;

        willDoNothing().given(orderService).cancel(orderId);

        mockMvc.perform(delete("/orders/{order_id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }
}
