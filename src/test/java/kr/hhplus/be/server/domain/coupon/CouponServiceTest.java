package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointRepository;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import kr.hhplus.be.server.support.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Test
    void 사용가능한_쿠폰_목록_조회(){
        Coupon coupon1 = Coupon.builder()
                .couponId(1L)
                .name("할인쿠폰")
                .status(Coupon.CouponStatus.INACTIVE)
                .build();
        Coupon coupon2 = Coupon.builder()
                .couponId(2L)
                .name("할인쿠폰")
                .status(Coupon.CouponStatus.ACTIVE)
                .build();
        List<Coupon> activeCoupons = List.of(coupon2);
        when(couponRepository.findAllByStatus(Coupon.CouponStatus.ACTIVE))
                .thenReturn(activeCoupons);

        // when
        List<Coupon> result = couponService.getCoupons(Coupon.CouponStatus.ACTIVE);

        // then
        assertEquals(1, result.size());
        assertEquals(Coupon.CouponStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void 조건없이_쿠폰목록_모두_조회(){
        Coupon coupon1 = Coupon.builder()
                .name("할인쿠폰")
                .totalCount(2000L)
                .status(Coupon.CouponStatus.INACTIVE)
                .build();
        Coupon coupon2 = Coupon.builder()
                .name("할인쿠폰")
                .totalCount(2000L)
                .status(Coupon.CouponStatus.ACTIVE)
                .build();

        List<Coupon> allCoupons = List.of(coupon1, coupon2);
        when(couponRepository.findAllCoupons()).thenReturn(allCoupons);

        // when
        List<Coupon> result = couponService.getCoupons(null);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void 쿠폰_상세_조회(){
        Coupon coupon1 = Coupon.builder()
                .couponId(1L)
                .name("할인 쿠폰")
                .build();
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon1));

        // when
        Coupon result = couponService.getCoupon(1L);

        // then
        assertEquals(Long.valueOf(1L), result.getCouponId());
        assertEquals("할인 쿠폰", result.getName());
    }

    @Test
    void 쿠폰_등록(){
        User user = new User("하늘", true);
        ReflectionTestUtils.setField(user, "userId", 1L);

        UserPoint point = new UserPoint(user.getUserId(), 1000L);

        when(userPointRepository.findByUserId(user.getUserId())).thenReturn(point);

        CouponCreateRequest request = mock(CouponCreateRequest.class);
        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(100L);
        when(request.getStartDate()).thenReturn(LocalDateTime.now());
        when(request.getEndDate()).thenReturn(LocalDateTime.now().plusDays(7));

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Coupon result = couponService.create(request, 1L);

        assertNotNull(result);
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void 쿠폰_등록시_관리자가_아닌경우_예외처리(){
        Long userId = 1L;
        User user = new User("하늘", false); // isAdmin = false
        ReflectionTestUtils.setField(user, "userId", userId);

        UserPoint point = new UserPoint(userId,1000L);

        when(userPointRepository.findByUserId(userId)).thenReturn(point);

        CouponCreateRequest request = mock(CouponCreateRequest.class);

        // when & then: 예외 발생 확인
        ForbiddenException e = assertThrows(
                ForbiddenException.class,
                () -> couponService.create(request, 1L)
        );

        assertEquals(ErrorCode.CREATE_COUPON_MUST_BE_ADMIN.getMessage(), e.getMessage());
    }

    @Test
    void 사용자_쿠폰_정상_발급(){
        Long userId = 1L;
        Long point = 200L;
        Long couponId = 100L;

        UserPoint user = new UserPoint(userId, point);
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .totalCount(1000L)
                .status(Coupon.CouponStatus.ACTIVE)
                .issuedCount(0L)
                .build();

        when(userPointRepository.findByUserId(userId)).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCoupon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserCoupon result = couponService.issue(userId, couponId);

        assertNotNull(result);
        verify(userCouponRepository).save(any(UserCoupon.class));
        verify(couponRepository).save(coupon);
    }

    @Test
    void 비활성화된_쿠폰_발급_실패_예외처리(){
        Long userId = 1L;
        Long point = 200L;
        Long couponId = 100L;

        UserPoint user = new UserPoint(userId, point);
        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .status(Coupon.CouponStatus.INACTIVE)
                .build();

        when(userPointRepository.findByUserId(userId)).thenReturn(user);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        assertThrows(IllegalArgumentException.class,
                () -> couponService.issue(userId, couponId));
    }

    @Test
    void 사용자_발급_쿠폰_목록조회(){
        Long userId = 1L;
        Long point = 200L;
        UserPoint user = new UserPoint(userId, point);

        UserCoupon coupon1 = mock(UserCoupon.class);
        UserCoupon coupon2 = mock(UserCoupon.class);
        List<UserCoupon> coupons = List.of(coupon1, coupon2);

        when(userPointRepository.findByUserId(userId)).thenReturn(user);
        when(userCouponRepository.findByUserId(userId)).thenReturn(coupons);

        List<UserCoupon> result = couponService.getUserCoupons(userId);

        assertEquals(2, result.size());
    }
}
