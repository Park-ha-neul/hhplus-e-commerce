package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
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
    private CouponEntityRepository couponEntityRepository;

    @Mock
    private UserCouponEntityRepository userCouponEntityRepository;

    @Mock
    private UserPointEntityRepository userPointEntityRepository;

    @Test
    void 사용가능한_쿠폰_목록_조회(){
        CouponEntity coupon1 = CouponEntity.builder()
                .couponId(1L)
                .name("할인쿠폰")
                .status(CouponStatus.INACTIVE)
                .build();
        CouponEntity coupon2 = CouponEntity.builder()
                .couponId(2L)
                .name("할인쿠폰")
                .status(CouponStatus.ACTIVE)
                .build();
        List<CouponEntity> activeCoupons = List.of(coupon2);
        when(couponEntityRepository.findAllByStatus(CouponStatus.ACTIVE))
                .thenReturn(activeCoupons);

        // when
        List<CouponEntity> result = couponService.getCoupons(CouponStatus.ACTIVE);

        // then
        assertEquals(1, result.size());
        assertEquals(CouponStatus.ACTIVE, result.get(0).getStatus());
    }

    @Test
    void 조건없이_쿠폰목록_모두_조회(){
        CouponEntity coupon1 = CouponEntity.builder()
                .name("할인쿠폰")
                .totalCount(2000L)
                .status(CouponStatus.INACTIVE)
                .build();
        CouponEntity coupon2 = CouponEntity.builder()
                .name("할인쿠폰")
                .totalCount(2000L)
                .status(CouponStatus.ACTIVE)
                .build();

        List<CouponEntity> allCoupons = List.of(coupon1, coupon2);
        when(couponEntityRepository.findAll()).thenReturn(allCoupons);

        // when
        List<CouponEntity> result = couponService.getCoupons(null);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void 쿠폰_상세_조회(){
        CouponEntity coupon1 = CouponEntity.builder()
                .couponId(1L)
                .name("할인 쿠폰")
                .build();
        when(couponEntityRepository.findById(1L)).thenReturn(Optional.of(coupon1));

        // when
        CouponEntity result = couponService.getCoupon(1L);

        // then
        assertEquals(Long.valueOf(1L), result.getCouponId());
        assertEquals("할인 쿠폰", result.getName());
    }

    @Test
    void 쿠폰_등록(){
        User user = new User(true);
        ReflectionTestUtils.setField(user, "userId", 1L);

        UserPoint point = new UserPoint(1000L);
        UserPointEntity userPointEntity = new UserPointEntity(user, point);

        when(userPointEntityRepository.findById(1L)).thenReturn(Optional.of(userPointEntity));

        CouponCreateRequest request = mock(CouponCreateRequest.class);
        when(request.getDiscountType()).thenReturn(DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(100L);
        when(request.getStartDate()).thenReturn(LocalDateTime.now());
        when(request.getEndDate()).thenReturn(LocalDateTime.now().plusDays(7));

        when(couponEntityRepository.save(any(CouponEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CouponEntity result = couponService.create(request, 1L);

        assertNotNull(result);
        verify(couponEntityRepository).save(any(CouponEntity.class));
    }

    @Test
    void 쿠폰_등록시_관리자가_아닌경우_예외처리(){
        User user = new User(false); // isAdmin = false
        ReflectionTestUtils.setField(user, "userId", 1L);

        UserPoint point = new UserPoint(1000L);
        UserPointEntity userPointEntity = new UserPointEntity(user, point);

        when(userPointEntityRepository.findById(1L)).thenReturn(Optional.of(userPointEntity));

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
        Long couponId = 100L;

        UserPointEntity user = UserPointEntity.builder().userId(userId).build();
        CouponEntity coupon = CouponEntity.builder()
                .couponId(couponId)
                .totalCount(1000L)
                .status(CouponStatus.ACTIVE)
                .issuedCount(0L)
                .build();

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponEntityRepository.findById(couponId)).thenReturn(Optional.of(coupon));
        when(userCouponEntityRepository.save(any(UserCouponEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserCouponEntity result = couponService.issueCouponToUser(userId, couponId);

        assertNotNull(result);
        verify(userCouponEntityRepository).save(any(UserCouponEntity.class));
        verify(couponEntityRepository).save(coupon);
    }

    @Test
    void 비활성화된_쿠폰_발급_실패_예외처리(){
        Long userId = 1L;
        Long couponId = 100L;

        UserPointEntity user = UserPointEntity.builder().userId(userId).build();
        CouponEntity coupon = CouponEntity.builder()
                .couponId(couponId)
                .status(CouponStatus.INACTIVE)
                .build();

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponEntityRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        assertThrows(IllegalArgumentException.class,
                () -> couponService.issueCouponToUser(userId, couponId));
    }

    @Test
    void 사용자_발급_쿠폰_목록조회(){
        Long userId = 1L;
        UserPointEntity user = UserPointEntity.builder().userId(userId).build();

        UserCouponEntity coupon1 = mock(UserCouponEntity.class);
        UserCouponEntity coupon2 = mock(UserCouponEntity.class);
        List<UserCouponEntity> coupons = List.of(coupon1, coupon2);

        when(userPointEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userCouponEntityRepository.findByUserId(userId)).thenReturn(coupons);

        List<UserCouponEntity> result = couponService.getUserCoupons(userId);

        assertEquals(2, result.size());
    }
}
