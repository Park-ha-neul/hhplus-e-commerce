package kr.hhplus.be.server.application.service.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Test
    void 쿠폰_정상_사용_성공(){
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setStatus(CouponStatus.ISSUED);

        when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

        // when
        userCouponService.useCoupon(1L);

        // then
        assertEquals(CouponStatus.USED, userCoupon.getStatus());
    }

    @Test
    void 이미_사용된_쿠폰이면_예외발생() {
        // given
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setStatus(CouponStatus.USED);

        when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userCouponService.useCoupon(1L));
    }

    @Test
    void 쿠폰_정상_발급() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        UserPoint user = new UserPoint(); // 필요시 setter 사용
        Coupon coupon = new Coupon();
        coupon.setTotalCount(10L);
        coupon.setIssuedCount(0L);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(false);

        // when
        userCouponService.issueCoupon(userId, couponId);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    void 이미_발급된_쿠폰인경우_예외처리() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        UserPoint user = new UserPoint();
        Coupon coupon = new Coupon();

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(true);

        // expect
        assertThrows(IllegalArgumentException.class, () ->
                userCouponService.issueCoupon(userId, couponId)
        );
    }

    @Test
    void 발급_한도_초과_예외처리() {
        // given
        Long userId = 1L;
        Long couponId = 100L;
        UserPoint user = new UserPoint();
        Coupon coupon = new Coupon();
        coupon.setTotalCount(1L);
        coupon.setIssuedCount(1L);

        when(userPointRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.existsByUserAndCoupon(user, coupon)).thenReturn(false);

        // expect
        assertThrows(IllegalArgumentException.class, () ->
                userCouponService.issueCoupon(userId, couponId)
        );
    }

    @Test
    void 사용자가_존재하지_않는경우_예외처리() {
        when(userPointRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userCouponService.issueCoupon(1L, 100L)
        );
    }

    @Test
    void 쿠폰_존재하지_않은경우_예외처리() {
        UserPoint user = new UserPoint();
        when(userPointRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(couponRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                userCouponService.issueCoupon(1L, 100L)
        );
    }

}
