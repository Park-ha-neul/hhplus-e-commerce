package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Test
    void 사용자_쿠폰_발급_성공(){
        User user = User.create(1L, false);
        Coupon coupon = mock(Coupon.class);
        UserCoupon userCoupon = UserCoupon.issueTo(user, coupon);

        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

        userCouponService.createUserCoupon(user, coupon);

        verify(userCouponRepository).save(any(UserCoupon.class));
    }

    @Test
    void 사용자_쿠폰_조회(){
        User user = User.create(1L, false);
        Long userId = user.getUserId();
        Coupon coupon = mock(Coupon.class);
        UserCoupon userCoupon = UserCoupon.issueTo(user, coupon);
        List<UserCoupon> couponList = List.of(userCoupon);

        when(userCouponRepository.findByUserAndCoupon(any(Long.class))).thenReturn(couponList);
        userCouponService.getUserCoupon(userId);

        verify(userCouponRepository).findByUserAndCoupon(any(Long.class));
    }

    @Test
    void 사용자_쿠폰_사용(){
        User user = User.create(1L, false);
        Coupon coupon = mock(Coupon.class);
        UserCoupon userCoupon = UserCoupon.issueTo(user, coupon);

        Long userCouponId = 1L;
        when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.of(userCoupon));

        userCouponService.useCoupon(userCouponId);

        verify(userCouponRepository).save(userCoupon);
    }
}
