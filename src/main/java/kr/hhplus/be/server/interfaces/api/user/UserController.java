package kr.hhplus.be.server.interfaces.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponResult;
import kr.hhplus.be.server.domain.coupon.UserCouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointHistoryResult;
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
    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 조회", description = "사용자 정보를 조회합니다.")
    public CustomApiResponse getUser(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        try{
            UserResult userResult = userService.getUser(userId);
            UserResponse response = UserResponse.from(userResult);
            return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
        } catch (Exception e){
            return CustomApiResponse.notFound(ApiMessage.INVALID_USER);
        }
    }

    @PostMapping("/")
    @Operation(summary = "사용자 등록", description = "처음 생성되는 사용자의 포인트는 0으로 초기화 됩니다.")
    public CustomApiResponse registPoint(@RequestBody UserCreateRequest request){
        try{
            UserResult userResult = userService.createUser(request.getUserName(), request.isAdmin());
            UserResponse response = UserResponse.from(userResult);
            return CustomApiResponse.create(ApiMessage.CREATE_SUCCESS, response);
        } catch (Exception e){
            System.out.println("e check : " + e);
            return CustomApiResponse.duplicateError(ApiMessage.CREATE_USER_ERROR);
        }
    }

    @GetMapping("/{userId}/histories")
    @Operation(summary = "사용자 포인트 이력 조회", description = "사용자 포인트 사용 이력을 조회합니다.")
    public CustomApiResponse getUserHistory(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        try{
            List<PointHistoryResult> pointHistory = userPointService.getUserPointHistory(userId);
            List<UserPointResponse> userPointResponseList = UserPointResponse.from(pointHistory);
            return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, userPointResponseList);
        } catch (RuntimeException e) {
            return CustomApiResponse.badRequest(ApiMessage.INVALID_USER);
        }
    }

    @GetMapping("/{userId}/coupons")
    @Operation(summary = "사용자 쿠폰 발급 목록 조회", description = "사용자가 발급받은 쿠폰 목록을 조회합니다.")
    public CustomApiResponse getUserCoupon(
            @PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId,
            @RequestParam(value = "status", required = false)UserCoupon.UserCouponStatus status
            ) {
        List<UserCouponResult> userCouponResultList = userCouponService.getUserCoupons(userId, status);
        List<UserCouponResponse> response = UserCouponResponse.from(userCouponResultList);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @PostMapping("/{userId}/coupons")
    @Operation(summary = "사용자 쿠폰 발급", description = "쿠폰 발급")
    public CustomApiResponse issueUserCoupon(
            @PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID")Long userId,
            @RequestBody IssueUserCouponRequest request
    ){
        try{
            userCouponService.issue(userId, request.getCouponId());
            return CustomApiResponse.create(ApiMessage.ISSUED_SUCCESS);
        } catch(Exception e){
            return CustomApiResponse.badRequest(ApiMessage.INVALID_COUPON);
        }
    }

    @GetMapping("/{userId}/orders")
    @Operation(summary = "사용자 주문 내역 조회", description = "사용자 주문 내역을 조회합니다.")
    public CustomApiResponse getUserOrder(
            @PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId,
            @RequestParam(value = "status", required = false)Order.OrderStatus status
    ){
        List<Order> orderList = orderService.getUserOrders(userId, status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, orderList);
    }

    @GetMapping("/{userId}/payments")
    @Operation(summary = "사용자 결제 내역 조회", description = "사용자 결제 내역을 조회합니다.")
    public CustomApiResponse getUserPayment(
            @PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId,
            @RequestParam(value = "status", required = false)Payment.PaymentStatus status
            ){
        List<Payment> paymentList = paymentService.getUserPayments(userId, status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, paymentList);
    }
}
