package kr.hhplus.be.server.interfaces.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "📌 쿠폰 관리", description = "쿠폰 관련 API 모음")
public class couponController {

    private final CouponService couponService;

    @GetMapping("/")
    @Operation(summary = "쿠폰 목록 조회", description = "쿠폰 목록을 조회합니다.")
    public CustomApiResponse getCoupons(
            @RequestParam(required = false) Coupon.CouponStatus status
    ){
        List<CouponResult> couponResultList = couponService.getCoupons(status);
        List<CouponResponse> couponResponses = CouponResponse.from(couponResultList);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, couponResponses);
    }


    @GetMapping("/{coupon_id}")
    @Operation(summary = "쿠폰 상세 조회", description = "쿠폰 상세 내용을 조회합니다.")
    public CustomApiResponse getProduct(@PathVariable("couponId") @Parameter(name = "couponId", description = "쿠폰 ID") Long couponId){
        CouponResponse response = CouponResponse.from(couponService.getCoupon(couponId));
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @PostMapping("/")
    @Operation(summary = "쿠폰 등록", description = "쿠폰을 등록합니다.")
    public CustomApiResponse createCoupon(
            @RequestBody CouponCreateRequest request,
            @RequestParam Long userId
    ){
        try{
            CouponCommand command = request.toCommand();
            CouponResult couponResult = couponService.create(command, userId);
            CouponResponse response = CouponResponse.from(couponResult);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, response);
        } catch (ForbiddenException e){
            return CustomApiResponse.forbidden(ApiMessage.FORBIDDEN_ACCESS);
        } catch (IllegalArgumentException e){
            return CustomApiResponse.badRequest(e.getMessage());
        } catch(Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    };
}
