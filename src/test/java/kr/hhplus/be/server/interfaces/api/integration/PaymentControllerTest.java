package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.facade.ProcessPayment;
import kr.hhplus.be.server.domain.payment.*;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ProcessPayment processPayment;

    @Test
    void 결제_생성_성공() throws Exception {
        // Given
        PaymentCommand command = new PaymentCommand(1L);
        PaymentResult mockPayment = new PaymentResult(1L, 1L, 1000L, Payment.PaymentStatus.COMPLETED, "");

        given(paymentService.create(any(PaymentCommand.class))).willReturn(mockPayment);

        // When & Then
        ResultActions result = mockMvc.perform(post("/payments/")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(command)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.totalAmount").value(1000L));
    }

    @Test
    void 결제_진행_성공() throws Exception {
        // Given
        Long paymentId = 1L;
        Payment mockPayment = new Payment(1L, 1000L);

        given(processPayment.processPayment(paymentId)).willReturn(mockPayment);

        // When & Then
        mockMvc.perform(post("/payments/{payment_id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.PAYMENT_SUCCESS));
    }

    @Test
    void 결제_목록_조회_성공() throws Exception {
        // Given
        List<Payment> mockPayments = List.of(
                new Payment(1L, 1000L),
                new Payment(2L, 2000L)
        );

        given(paymentService.getPayments(Payment.PaymentStatus.PENDING)).willReturn(mockPayments);

        // When & Then
        mockMvc.perform(get("/payments/")
                        .param("status", Payment.PaymentStatus.PENDING.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].orderId").value(1L))
                .andExpect(jsonPath("$.data[1].orderId").value(2L));
    }

    @Test
    void 결제_상세_조회_성공() throws Exception {
        // Given
        Long paymentId = 1L;
        Payment mockPayment = new Payment(1L, 1000L);

        given(paymentService.getPayment(paymentId)).willReturn(mockPayment);

        // When & Then
        mockMvc.perform(get("/payments/{payment_id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.orderId").value(paymentId));
    }


}
