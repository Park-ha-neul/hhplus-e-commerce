package kr.hhplus.be.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.ErrorCode;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryResult;
import kr.hhplus.be.server.domain.user.UserErrorCode;
import kr.hhplus.be.server.domain.user.UserPointService;
import kr.hhplus.be.server.domain.user.UserResult;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.api.user.IssueUserCouponRequest;
import kr.hhplus.be.server.interfaces.api.user.UserCouponResponse;
import kr.hhplus.be.server.interfaces.api.user.UserCreateRequest;
import kr.hhplus.be.server.interfaces.api.user.UserPointResponse;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserPointService userPointService;

    @MockitoBean
    private UserCouponService userCouponService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void 사용자_조회_성공() throws Exception {
        // given
        UserResult mockUserResult = new UserResult( 1L, "하늘", false, 100L);
        given(userService.getUser(1L)).willReturn(mockUserResult);

        // when & then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.userName").value("하늘"))
                .andExpect(jsonPath("$.data.admin").value(false))
                .andExpect(jsonPath("$.data.point").value(100L))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS));
    }

    @Test
    void 사용자_조회_실패_404() throws Exception{
        //given
        given(userService.getUser(9999L)).willReturn(null);

        // when & then
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(ApiMessage.INVALID_USER));
    }

    @Test
    void 사용자_등록_성공() throws Exception{
        //given
        UserCreateRequest request = new UserCreateRequest("하늘", false);
        UserResult mockUserResult = new UserResult(1L, "하늘", false, 0L);
        given(userService.createUser("하늘", false)).willReturn(mockUserResult);

        // when & then
        mockMvc.perform(post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.CREATE_SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.CREATE_SUCCESS))
                .andExpect(jsonPath("$.data.userName").value("하늘"))
                .andExpect(jsonPath("$.data.admin").value(false))
                .andExpect(jsonPath("$.data.point").value(0));
    }

    @Test
    void 사용자_등록_오류_발생_409() throws Exception {
        UserCreateRequest request = new UserCreateRequest("하늘", false);
        given(userService.createUser("하늘", false))
                .willThrow(new IllegalArgumentException(UserErrorCode.USER_CREATE_ERROR.getMessage()));

        // when & then
        mockMvc.perform(post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_ERROR))
                .andExpect(jsonPath("$.message").value(ApiMessage.CREATE_USER_ERROR));
    }

    @Test
    void 사용자_포인트_이력_조회_성공() throws Exception{
        Long userId = 1L;
        List<PointHistoryResult> pointHistory = Arrays.asList(
                new PointHistoryResult(1L, 200L,  2000L, 1800L, PointHistory.TransactionType.USE),
                new PointHistoryResult(1L, 300L, 3000L, 3300L, PointHistory.TransactionType.CHARGE)
        );
        List<UserPointResponse> responseList = UserPointResponse.from(pointHistory);

        given(userPointService.getUserPointHistory(userId)).willReturn(pointHistory);

        // when & then
        mockMvc.perform(get("/users/{userId}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(responseList.size()));
    }

    @Test
    void 사용자_포인트_이력_조회_실패_401() throws Exception{
        // given
        Long userId = 1L;
        given(userPointService.getUserPointHistory(userId)).willThrow(new RuntimeException("사용자 정보 없음"));

        // when & then
        mockMvc.perform(get("/users/{userId}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ApiMessage.INVALID_USER));
    }

    @Test
    void 쿠폰_발급_성공() throws Exception{
        // given
        Long userId = 1L;
        IssueUserCouponRequest request = new IssueUserCouponRequest(1L);
        UserCouponResult userCouponResult = new UserCouponResult(1L, userId, 1L, UserCoupon.UserCouponStatus.ISSUED);
        UserCouponResponse response = UserCouponResponse.from(userCouponResult);

        given(userCouponService.issue(userId, request.getCouponId())).willReturn(userCouponResult);

        // when & then
        mockMvc.perform(post("/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.CREATE_SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.ISSUED_SUCCESS))
                .andExpect(jsonPath("$.data.id").value(response.getId()));
    }

    @Test
    void 사용자_쿠폰_발급_목록_조회() throws Exception{
        // given
        Long userId = 1L;
        UserCoupon.UserCouponStatus status = UserCoupon.UserCouponStatus.ISSUED;
        List<UserCouponResult> userCouponResultList = Arrays.asList(new UserCouponResult(1L, userId, 1L, status));
        List<UserCouponResponse> response = UserCouponResponse.from(userCouponResultList);

        given(userCouponService.getUserCoupons(userId, status)).willReturn(userCouponResultList);

        // when & then
        mockMvc.perform(get("/users/{userId}/coupons", userId).param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(response.size()));
    }

    @Test
    void 쿠폰_발급_실패_401() throws Exception{
        Long userId = 1L;
        IssueUserCouponRequest request = new IssueUserCouponRequest(1L);
        given(userCouponService.issue(userId, request.getCouponId()))
                .willThrow(new IllegalArgumentException(ErrorCode.COUPON_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ApiMessage.INVALID_COUPON));
    }

    @Test
    void 사용자_주문_내역_조회_성공() throws Exception{
        // given
        Long userId = 1L;
        Order.OrderStatus status = Order.OrderStatus.SUCCESS;
        List<Order> orderList = List.of(new Order());

        given(orderService.getUserOrders(userId, status)).willReturn(orderList);

        // when & then
        mockMvc.perform(get("/users/{userId}/orders", userId)
                        .param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(orderList.size()));
    }

    @Test
    void 사용자_결제_내역_조회_성공() throws Exception{
        // given
        Long userId = 1L;
        Payment.PaymentStatus status = Payment.PaymentStatus.COMPLETED;
        List<Payment> paymentList = List.of(new Payment());

        given(paymentService.getUserPayments(userId, status)).willReturn(paymentList);

        // when & then
        mockMvc.perform(get("/users/{userId}/payments", userId)
                        .param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(paymentList.size()));
    }
}
