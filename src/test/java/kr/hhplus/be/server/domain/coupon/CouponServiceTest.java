package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
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
        List<Coupon> coupons = couponService.getCoupons(status);

        // Assert
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals(status, coupons.get(0).getStatus());
    }

    @Test
    public void 쿠폰_아이디로_조회_성공() {
        // Arrange
        Long couponId = 1L;
        Coupon mockCoupon1 = mock(Coupon.class);
        when(mockCoupon1.getCouponId()).thenReturn(1L);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon1));

        // Act
        Coupon coupon = couponService.getCoupon(couponId);

        // Assert
        assertNotNull(coupon);
        assertEquals(Long.valueOf(1L), coupon.getCouponId());
    }

    @Test
    public void 쿠폰_생성_성공() {
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("할인쿠폰")
                .totalCount(100L)
                .discountType(Coupon.DiscountType.RATE)
                .discountRate(10L)
                .discountAmount(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        when(userRepository.findById(1L)).thenReturn(mockUser);
        when(couponRepository.save(any(Coupon.class))).thenReturn(mockCoupon);

        // Act
        Coupon createdCoupon = couponService.create(request, 1L);

        // Assert
        assertNotNull(createdCoupon);
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    public void 쿠폰_생성_권한_없는_사용자() {
        // Arrange
        User mockUser = new User("하늘", false);
        when(userRepository.findById(anyLong())).thenReturn(mockUser);
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("할인쿠폰")
                .totalCount(100L)
                .discountType(Coupon.DiscountType.RATE)
                .discountRate(10L)
                .discountAmount(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // Act & Assert
        assertThrows(ForbiddenException.class, () -> couponService.create(request, 1L));
    }
}
