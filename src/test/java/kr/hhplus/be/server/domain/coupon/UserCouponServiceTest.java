package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepository;

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
    @Transactional
    public void 쿠폰_발급_성공() {
        // Arrange
        Long userId = 1L;
        Long couponId = 1L;
        when(userRepository.findById(userId)).thenReturn(mockUser);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(UserCoupon.create(userId, couponId));

        // Act
        UserCouponResult userCouponResult = userCouponService.issue(userId, couponId);

        // Assert
        assertNotNull(userCouponResult);
        assertEquals(userId, userCouponResult.getUserId());
        assertEquals(couponId, userCouponResult.getCouponId());
    }

    @Test
    public void 쿠폰_사용_성공() {
        // Arrange
        Long userCouponId = 1L;
        UserCoupon mockUserCoupon = UserCoupon.create(1L, 1L);
        when(userCouponRepository.findById(userCouponId)).thenReturn(Optional.of(mockUserCoupon));

        userCouponService.use(userCouponId);

        // Assert
        assertEquals(UserCoupon.UserCouponStatus.USED, mockUserCoupon.getStatus());
        verify(userCouponRepository, times(1)).save(mockUserCoupon);
    }
}
