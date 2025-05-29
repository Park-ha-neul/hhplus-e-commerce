package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

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
        mockCoupon = new Coupon("Test Coupon", 100L, 0L, Coupon.DiscountType.RATE, 10L, 0L, Coupon.CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void 쿠폰_발급_성공() {
        Long userId = 1L;
        Long couponId = 1L;
        String issuedSetKey = "coupon:" + couponId + ":users";
        String stockListKey = "coupon:" + couponId;

        // Set에 정상적으로 추가된 경우
        when(setOperations.add(eq(issuedSetKey), eq(String.valueOf(userId)))).thenReturn(1L);
        // 재고 리스트에서 하나 꺼냄
        when(listOperations.leftPop(stockListKey)).thenReturn("dummyCouponStock");

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));

        userCouponService.issue(userId, couponId);

        verify(setOperations).add(issuedSetKey, String.valueOf(userId));
        verify(listOperations).leftPop(stockListKey);
        verify(redisTemplate).expire(eq(issuedSetKey), anyLong(), eq(TimeUnit.SECONDS));
    }

    @Test
    public void 쿠폰_발급_실패_쿠폰없음_IllegalArgumentException() {
        // Arrange
        Long userId = 1L;
        Long couponId = 10L;
        Coupon coupon = mock(Coupon.class);

        given(userRepository.findById(userId)).willReturn(mockUser);
        given(couponRepository.findById(couponId)).willReturn(Optional.empty());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.issue(userId, couponId);
        });

        // then
        assertEquals(ErrorCode.COUPON_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    public void 쿠폰_발급_실패_비활성화_IllegalArgumentException() {
        // Arrange
        Long userId = 1L;
        Long couponId = 10L;
        Coupon coupon = mock(Coupon.class);

        given(userRepository.findById(userId)).willReturn(mockUser);
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(coupon.getStatus()).willReturn(Coupon.CouponStatus.INACTIVE);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.issue(userId, couponId);
        });

        // then
        assertEquals(ErrorCode.INACTIVE_COUPON.getMessage(), e.getMessage());
    }

    @Test
    public void 쿠폰_발급_실패_발급수초과_IllegalArgumentException() {
        Long userId = 1L;
        Long couponId = 10L;
        Coupon coupon = mock(Coupon.class);

        given(userRepository.findById(userId)).willReturn(mockUser);
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(coupon.getStatus()).willReturn(Coupon.CouponStatus.ACTIVE);
        given(coupon.getIssuedCount()).willReturn(10L);
        given(coupon.getTotalCount()).willReturn(10L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.issue(userId, couponId);
        });

        // then
        assertEquals(ErrorCode.COUPON_ISSUED_EXCEED.getMessage(), e.getMessage());
    }

    @Test
    public void 쿠폰_사용_성공() {
        // Arrange
        Long userCouponId = 1L;
        UserCoupon mockUserCoupon = UserCoupon.create(1L, 1L);
        when(userCouponRepository.findById(userCouponId)).thenReturn(mockUserCoupon);

        userCouponService.use(userCouponId);

        // Assert
        assertEquals(UserCoupon.UserCouponStatus.USED, mockUserCoupon.getStatus());
        verify(userCouponRepository, times(1)).save(mockUserCoupon);
    }

    @Test
    public void 쿠폰_사용_실패_존재하지_않는_쿠폰_IllegalArgumentException() {
        // Arrange
        Long userCouponId = 1L;
        UserCoupon mockUserCoupon = UserCoupon.create(1L, 1L);
        given(userCouponRepository.findById(userCouponId)).willReturn(mockUserCoupon);

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.use(userCouponId);
        });

        // then
        assertEquals(ErrorCode.USER_COUPON_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    public void 쿠폰_사용_실패_이미_사용한_쿠폰_IllegalArgumentException() {
        // Arrange
        Long userCouponId = 2L;
        UserCoupon usedCoupon = mock(UserCoupon.class);
        given(userCouponRepository.findById(userCouponId)).willReturn(usedCoupon);
        given(usedCoupon.getStatus()).willReturn(UserCoupon.UserCouponStatus.USED);

        // when
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            userCouponService.use(userCouponId);
        });

        // then
        assertEquals(ErrorCode.ALREADY_USED_COUPON.getMessage(), e.getMessage());
    }
}
