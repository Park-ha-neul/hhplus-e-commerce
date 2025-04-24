package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserRepository userRepository;

    private User mockUser;
    private Coupon mockCoupon;

    @BeforeEach
    public void setUp() {
        // Mocking User
        mockUser = new User("하늘", true);

        // Mocking Coupon
        mockCoupon = new Coupon("Test Coupon", 100L, 0L, Coupon.DiscountType.RATE, 10L, 0L, Coupon.CouponStatus.ACTIVE, null, null);
    }

    @Test
    public void 쿠폰_상태로_조회_성공() {
        // Arrange
        Coupon.CouponStatus status = Coupon.CouponStatus.ACTIVE;
        when(couponRepository.findAllByStatus(status)).thenReturn(List.of(mockCoupon));

        // Act
        List<CouponResult> couponResults = couponService.getCoupons(status);

        // Assert
        assertNotNull(couponResults);
        assertEquals(1, couponResults.size());
        assertEquals(status, couponResults.get(0).getStatus());
    }

    @Test
    public void 쿠폰_아이디로_조회_성공() {
        // Arrange
        Long couponId = 1L;
        Coupon mockCoupon1 = mock(Coupon.class);
        when(mockCoupon1.getCouponId()).thenReturn(1L);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon1));

        // Act
        CouponResult couponResult = couponService.getCoupon(couponId);

        // Assert
        assertNotNull(couponResult);
        assertEquals(Long.valueOf(1L), couponResult.getId());
    }

    @Test
    public void 쿠폰_생성_성공() {
        CouponCommand command = new CouponCommand(
                "할인쿠폰",
                100L,
                Coupon.DiscountType.RATE,
                10L,
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );

        when(userRepository.findById(1L)).thenReturn(mockUser);
        when(couponRepository.save(any(Coupon.class))).thenReturn(mockCoupon);

        // Act
        CouponResult couponResult = couponService.create(command, 1L);

        // Assert
        assertNotNull(couponResult);
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    public void 쿠폰_생성_권한_없는_사용자() {
        // Arrange
        User mockUser = new User("하늘", false);
        when(userRepository.findById(anyLong())).thenReturn(mockUser);
        CouponCommand command = new CouponCommand(
                "할인쿠폰",
                100L,
                Coupon.DiscountType.RATE,
                10L,
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> couponService.create(command, 1L));
    }
}
