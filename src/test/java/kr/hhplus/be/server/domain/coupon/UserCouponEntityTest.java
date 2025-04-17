package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserCouponEntityTest {
    @Test
    void 쿠폰_정상_발급(){
        User user = new User(false);
        UserPoint point = new UserPoint(1000L);
        UserPointEntity userPointEntity = new UserPointEntity(user, point);
        CouponEntity coupon = mock(CouponEntity.class);

        UserCouponEntity result = UserCouponEntity.create(userPointEntity, coupon);

        assertEquals(UserCouponStatus.ISSUED, result.getStatus());
    }

    @Test
    void 쿠폰_사용(){
        User user = new User(false);
        UserPoint point = new UserPoint(1000L);
        UserPointEntity userPointEntity = new UserPointEntity(user, point);
        CouponEntity coupon = mock(CouponEntity.class);

        UserCouponEntity userCouponEntity = UserCouponEntity.create(userPointEntity, coupon);
        userCouponEntity.use();

        assertEquals(UserCouponStatus.USED, userCouponEntity.getStatus());
    }

    @Test
    void 이미_사용된_쿠폰_예외발생(){
        User user = new User(false);
        UserPoint point = new UserPoint(1000L);
        UserPointEntity userPointEntity = new UserPointEntity(user, point);
        CouponEntity coupon = mock(CouponEntity.class);

        UserCouponEntity userCouponEntity = UserCouponEntity.create(userPointEntity, coupon);
        userCouponEntity.use();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponEntity.use();
        });

        assertEquals(ErrorCode.ALREADY_USED_COUPON.getMessage(), e.getMessage());
    }
}
