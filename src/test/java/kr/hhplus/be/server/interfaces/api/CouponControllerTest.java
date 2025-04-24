package kr.hhplus.be.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import kr.hhplus.be.server.interfaces.api.coupon.CouponResponse;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ForbiddenException;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private UserService userService;

    @Test
    void 쿠폰_목록_조회_성공() throws Exception {
        // Given
        List<CouponResult> couponResults = List.of(
                new CouponResult(1L, "Coupon 1", 200L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7)),
                new CouponResult(2L, "Coupon 2", 200L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.INACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7))
        );
        List<CouponResponse> couponResponses = CouponResponse.from(couponResults);

        given(couponService.getCoupons(Coupon.CouponStatus.ACTIVE)).willReturn(couponResults);

        // When & Then
        mockMvc.perform(get("/coupons/")
                        .param("status", Coupon.CouponStatus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L));
    }

    @Test
    void 쿠폰_상세_조회_성공() throws Exception {
        // Given
        Long couponId = 1L;
        CouponResult couponResult = new CouponResult(1L, "Coupon 1", 200L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        CouponResponse couponResponse = CouponResponse.from(couponResult);

        given(couponService.getCoupon(couponId)).willReturn(couponResult);

        // When & Then
        mockMvc.perform(get("/coupons/{coupon_id}", couponId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(couponId))
                .andExpect(jsonPath("$.data.name").value("Coupon 1"));
    }

    @Test
    void 쿠폰_등록_성공() throws Exception {
        // Given
        CouponCreateRequest request = new CouponCreateRequest();
        Long userId = 1L;

        CouponCommand command = request.toCommand();
        CouponResult couponResult = new CouponResult(1L, "Coupon 1", 200L, Coupon.DiscountType.AMOUNT, null, 100L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        CouponResponse couponResponse = CouponResponse.from(couponResult);

        User adminUser = new User("관리자", true);
        given(userService.getUserEntity(userId)).willReturn(adminUser);
        given(couponService.create(any(CouponCommand.class), any(Long.class))).willReturn(couponResult);

        // When & Then
        mockMvc.perform(post("/coupons/")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Coupon 1"));
    }

    @Test
    void 쿠폰_등록_실패_403() throws Exception {
        // Given
        CouponCreateRequest request = new CouponCreateRequest();
        Long userId = 1L;

        given(couponService.create(any(CouponCommand.class), any(Long.class)))
                .willThrow(new ForbiddenException(ApiMessage.FORBIDDEN_ACCESS));

        // When & Then
        mockMvc.perform(post("/coupons/")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(ApiMessage.FORBIDDEN_ACCESS));
    }

    @Test
    void 쿠폰_등록_실패_500() throws Exception {
        // Given
        CouponCreateRequest request = new CouponCreateRequest();
        Long userId = 1L;

        given(couponService.create(any(CouponCommand.class), any(Long.class)))
                .willThrow(new RuntimeException("서버 오류"));

        // When & Then
        mockMvc.perform(post("/coupons/")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(request))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SERVER_ERROR))
                .andExpect(jsonPath("$.message").value(ApiMessage.SERVER_ERROR));
    }
}
