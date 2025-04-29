package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 쿠폰_목록_조회_성공() throws Exception {
        Coupon coupon = new Coupon("Coupon 1", 200L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        Coupon coupon1 = new Coupon("Coupon 2", 200L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.INACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon1);

        // When & Then
        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].id").value(coupon.getCouponId()))
                .andExpect(jsonPath("$.data[1].id").value(coupon1.getCouponId()));
    }

    @Test
    void 쿠폰_상세_조회_성공() throws Exception {
        // Given
        Coupon coupon = new Coupon("Coupon 1", 200L, 0L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        Long couponId = coupon.getCouponId();


        // When & Then
        mockMvc.perform(get("/coupons/{coupon_id}", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(couponId))
                .andExpect(jsonPath("$.data.name").value(coupon.getName()));
    }

    @Test
    void 쿠폰_등록_성공() throws Exception {
        // Given
        CouponCommand command = new CouponCommand("Coupon 1", 200L, Coupon.DiscountType.AMOUNT, null, 100L, LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        User adminUser = new User("관리자", true);
        userRepository.save(adminUser);
        Long userId = adminUser.getUserId();

        // When & Then
        mockMvc.perform(post("/coupons")
                        .contentType("application/json")
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(command))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.name").value(command.getName()));
    }

    @Test
    void 쿠폰_등록_실패_403() throws Exception {
        // Given
        CouponCommand command = new CouponCommand("Coupon 1", 200L, Coupon.DiscountType.AMOUNT, null, 100L, LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        User user = new User("사용자", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        // When & Then
        mockMvc.perform(post("/coupons")
                        .contentType("application/json")
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(command))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(ApiMessage.FORBIDDEN_ACCESS));
    }
}
