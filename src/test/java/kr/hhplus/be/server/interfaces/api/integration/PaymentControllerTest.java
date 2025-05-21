package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.*;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 결제_금액_미리보기_성공() throws Exception {
        // Given
        Order order = new Order(1L, 1L);
        orderRepository.save(order);
        PaymentPreviewCommand command = new PaymentPreviewCommand(order.getOrderId());

        mockMvc.perform(
                post("/payments/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(command))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.orderId").value(command.getOrderId()));
    }

    @Test
    void 결제_진행_성공() throws Exception {
        // Given
        Long orderId = 1L;
        Long totalAmount = 1000L;
        PaymentCommand command = new PaymentCommand(orderId, totalAmount);

        // When & Then
        mockMvc.perform(
                post("/payments/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(command))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.PAYMENT_SUCCESS));
    }

    @Test
    void 결제_목록_조회_성공() throws Exception {
        // Given
        paymentRepository.deleteAll();
        Payment payment1 = new Payment(1L, 1000L);
        paymentRepository.save(payment1);
        Payment payment2 = new Payment(2L, 2000L);
        paymentRepository.save(payment2);

        // When & Then
        mockMvc.perform(get("/payments/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].orderId").value(payment1.getOrderId()))
                .andExpect(jsonPath("$.data[1].orderId").value(payment2.getOrderId()));
    }

    @Test
    void 결제_상세_조회_성공() throws Exception {
        // Given
        Payment payment = new Payment(1L, 1000L);
        paymentRepository.save(payment);
        Long paymentId = payment.getPaymentId();

        // When & Then
        mockMvc.perform(get("/payments/{payment_id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.orderId").value(payment.getOrderId()));
    }


}
