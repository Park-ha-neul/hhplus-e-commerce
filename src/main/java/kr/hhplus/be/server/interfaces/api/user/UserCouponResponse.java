package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.coupon.UserCouponResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserCouponResponse {
    private Long id;
    private Long userId;
    private Long couponId;
    private UserCoupon.UserCouponStatus status;

    public static UserCouponResponse from(UserCouponResult userCouponResult) {
        return new UserCouponResponse(
                userCouponResult.getId(),
                userCouponResult.getUserId(),
                userCouponResult.getCouponId(),
                userCouponResult.getStatus()
        );
    }

    public static List<UserCouponResponse> from(List<UserCouponResult> resultList){
        return resultList.stream()
                .map(UserCouponResponse::from)
                .collect(Collectors.toList());
    }
}
