package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Test
    void 주문_등록_성공() throws Exception {
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Coupon coupon = new Coupon("Coupon 1", 200L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        UserCoupon userCoupon = new UserCoupon(user.getUserId(), coupon.getCouponId(), UserCoupon.UserCouponStatus.ISSUED);
        userCouponRepository.save(userCoupon);
        Long userCouponId = userCoupon.getUserCouponId();

        OrderRequest request = new OrderRequest(
                userId,
                userCouponId,
                List.of(new OrderItemRequest(1L, 2L, 1000L))
        );

        // 실제 요청 및 응답 검증
        mockMvc.perform(post("/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 주문_등록_실패_재고부족() throws Exception {
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Coupon coupon = new Coupon("Coupon 1", 200L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        UserCoupon userCoupon = new UserCoupon(user.getUserId(), coupon.getCouponId(), UserCoupon.UserCouponStatus.ISSUED);
        userCouponRepository.save(userCoupon);
        Long userCouponId = userCoupon.getUserCouponId();

        OrderRequest request = new OrderRequest(
                userId,
                userCouponId,
                List.of(new OrderItemRequest(1L, 200000000L, 3000L))
        );

        // 실제 요청 및 응답 검증
        mockMvc.perform(post("/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ProductErrorCode.NOT_ENOUGH_STOCK.getMessage()));
    }

    @Test
    void 주문_목록_조회_성공() throws Exception {
        mockMvc.perform(get("/orders/")
                        .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS));
    }

    @Test
    void 주문_상세_조회_성공() throws Exception {
        Order order = new Order(1L, 1L);
        orderRepository.save(order);
        Long orderId = order.getOrderId();

        mockMvc.perform(get("/orders/{order_id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    void 주문_취소_성공() throws Exception {
        Order order = new Order(1L, 1L);
        orderRepository.save(order);
        Long orderId = order.getOrderId();

        mockMvc.perform(delete("/orders/{order_id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }
}
