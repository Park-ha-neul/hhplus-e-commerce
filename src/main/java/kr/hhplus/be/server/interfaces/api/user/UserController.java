package kr.hhplus.be.server.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.user.*;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "사용자 관리", description = "사용자 포인트 조회, 사용자 등록")
public class UserController {

    private final UserService userService;
    private final UserPointService userPointService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회", description = "사용자 정보를 조회합니다.")
    public CustomApiResponse getUser(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        UserWithPointResponse data = userService.getUser(userId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @PostMapping("/")
    @Operation(summary = "사용자 등록", description = "처음 생성되는 사용자의 포인트는 0으로 초기화 됩니다.")
    public CustomApiResponse registPoint(@RequestBody UserRegisterRequest request){
        User user = userService.createUser(request.getUserName(), request.isAdmin());
        return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, user);
    }

    @GetMapping("/{userId}/histories")
    @Operation(summary = "사용자 포인트 이력 조회", description = "사용자 포인트 사용 이력을 조회합니다.")
    public CustomApiResponse getUserHistory(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        try{
            List<PointHistory> pointHistory = userPointService.getUserPointHistory(userId);
            return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, pointHistory);
        } catch (RuntimeException e) {
            return CustomApiResponse.badRequest(ApiMessage.INVALID_USER);
        }
    }

    @GetMapping("/{userId}/coupons")
    @Operation(summary = "사용자 쿠폰 발급 목록 조회", description = "사용자가 발급받은 쿠폰 목록을 조회합니다.")
    public CustomApiResponse getUserCoupon(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId) {
        try{
            List<UserCouponEntity> userCouponEntityList = couponService.getUserCoupons(userId);
            return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, userCouponEntityList);
        } catch(IllegalArgumentException e){
            return CustomApiResponse.badRequest(ApiMessage.INVALID_USER);
        }
    }

    @PostMapping("/{userId}/coupons")
    @Operation(summary = "사용자 쿠폰 발급", description = "쿠폰 발급")
    public CustomApiResponse issueUserCoupon(
            @PathVariable Long userId,
            @RequestBody IssueUserCouponRequest request
    ){
        UserCouponEntity result = couponService.issue(userId, request.getCouponId());
        return CustomApiResponse.success(ApiMessage.ISSUED_SUCCESS, result);
    }

    @GetMapping("/{userId}/orders")
    @Operation(summary = "사용자 주문 내역 조회", description = "사용자 주문 내역을 조회합니다.")
    public CustomApiResponse getUserOrder(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        List<OrderEntity> orderEntityList = orderService.getOrderByUserId(userId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, orderEntityList);
    }

    @GetMapping("/{userId}/payments")
    @Operation(summary = "사용자 결제 내역 조회", description = "사용자 결제 내역을 조회합니다.")
    public CustomApiResponse getUserPayment(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        List<PaymentEntity> paymentEntityList = paymentService.getPaymentByUserId(userId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, paymentEntityList);
    }
}
