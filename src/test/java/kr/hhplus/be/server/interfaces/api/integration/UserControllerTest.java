package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.*;
import kr.hhplus.be.server.interfaces.api.user.IssueUserCouponRequest;
import kr.hhplus.be.server.interfaces.api.user.UserCreateRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 사용자_조회_성공() throws Exception {
        // given
        User user = userRepository.findById(1L);
        UserPoint point = userPointRepository.findByUserId(user.getUserId());

        // when & then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(user.getUserId()))
                .andExpect(jsonPath("$.data.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.data.admin").value(user.getAdminYn()))
                .andExpect(jsonPath("$.data.point").value(point.getPoint()))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS));
    }

    @Test
    void 사용자_조회_실패_404() throws Exception{
        //given
        User user = new User("하늘", false);
        userRepository.save(user);
        UserPoint userPoint = new UserPoint(user.getUserId(), 1000L);
        userPointRepository.save(userPoint);

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

        // when & then
        mockMvc.perform(post("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.CREATE_SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.CREATE_SUCCESS))
                .andExpect(jsonPath("$.data.userName").value(request.getUserName()))
                .andExpect(jsonPath("$.data.admin").value(request.isAdmin()))
                .andExpect(jsonPath("$.data.point").value(0));
    }

    @Test
    void 사용자_포인트_이력_조회_성공() throws Exception{
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();
        PointHistory pointHistory1 = new PointHistory(userId, 200L, 2000L, 1800L, PointHistory.TransactionType.USE);
        PointHistory pointHistory2 = new PointHistory(userId, 300L, 3000L, 3300L, PointHistory.TransactionType.CHARGE);

        pointHistoryRepository.save(pointHistory1);
        pointHistoryRepository.save(pointHistory2);

        // when & then
        mockMvc.perform(get("/users/{userId}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void 쿠폰_발급_성공() throws Exception{
        // given
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Coupon coupon = new Coupon("쿠폰 1", 2000L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        IssueUserCouponRequest request = new IssueUserCouponRequest(coupon.getCouponId());

        // when & then
        mockMvc.perform(post("/users/{userId}/coupons", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.CREATE_SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.ISSUED_SUCCESS))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void 사용자_쿠폰_발급_목록_조회() throws Exception{
        // given
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Coupon coupon = new Coupon("쿠폰 1", 2000L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        Long couponId = coupon.getCouponId();

        UserCoupon userCoupon = new UserCoupon(userId, couponId, UserCoupon.UserCouponStatus.ISSUED);
        userCouponRepository.save(userCoupon);

        Coupon coupon2 = new Coupon("쿠폰 2", 2000L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon2);
        Long couponId2 = coupon2.getCouponId();

        UserCoupon userCoupon2 = new UserCoupon(userId, couponId2, UserCoupon.UserCouponStatus.ISSUED);
        userCouponRepository.save(userCoupon2);

        List<UserCoupon> result = userCouponRepository.findByUserId(userId);

        UserCoupon.UserCouponStatus status = UserCoupon.UserCouponStatus.ISSUED;

        // when & then
        mockMvc.perform(get("/users/{userId}/coupons", userId).param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(result.size()));
    }

    @Test
    void 쿠폰_발급_실패_401() throws Exception{

        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Coupon coupon = new Coupon("쿠폰 1", 2000L, 0L, Coupon.DiscountType.RATE, 10L, null, Coupon.CouponStatus.INACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);
        Long couponId = coupon.getCouponId();

        IssueUserCouponRequest request = new IssueUserCouponRequest(coupon.getCouponId());

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
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Order order = new Order(user.getUserId(), null);
        orderRepository.save(order);

        List<Order> result = orderRepository.findByUserId(userId);

        // when & then
        mockMvc.perform(get("/users/{userId}/orders", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS))
                .andExpect(jsonPath("$.data.length()").value(result.size()));
    }

    @Test
    void 사용자_결제_내역_조회_성공() throws Exception{
        // given
        User user = new User("하늘", false);
        userRepository.save(user);
        Long userId = user.getUserId();

        Order order = new Order(user.getUserId(), null);
        orderRepository.save(order);
        Long orderId = order.getOrderId();

        Payment payment = new Payment(orderId, 2000L);
        paymentRepository.save(payment);

        Payment.PaymentStatus status = Payment.PaymentStatus.COMPLETED;

        // when & then
        mockMvc.perform(get("/users/{userId}/payments", userId)
                        .param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(ApiMessage.VIEW_SUCCESS));
    }
}
