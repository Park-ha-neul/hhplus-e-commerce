package kr.hhplus.be.server.application.service.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void 쿠폰을_정상적으로_등록한다(){
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("10% 할인 쿠폰");
        request.setTotalCount(100L);
        request.setDiscountType(DiscountType.RATE);
        request.setDiscountRate(10L);
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(7));

        Coupon expectedCoupon = Coupon.createFrom(request);

        when(couponRepository.save(any(Coupon.class))).thenReturn(expectedCoupon);

        // when
        Coupon result = couponService.createCoupon(request);

        // then
        assertNotNull(result);
        assertEquals("10% 할인 쿠폰", result.getName());
        assertEquals(Long.valueOf(100L), result.getTotalCount());
        assertEquals(DiscountType.RATE, result.getDiscountType());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());

        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void 할인타입이_rate인데_할인율이_없는경우_예외처리(){
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("비정상 쿠폰");
        request.setTotalCount(100L);
        request.setDiscountType(DiscountType.RATE);
        request.setDiscountRate(null);

        // when & then
        assertThatThrownBy(() -> Coupon.createFrom(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("할인율이 필요합니다.");
    }

    @Test
    void 할인타입이_amount인데_할인금액이_없는경우_예외처리(){
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("비정상 쿠폰");
        request.setTotalCount(100L);
        request.setDiscountType(DiscountType.AMOUNT);
        request.setDiscountAmount(null);

        // when & then
        assertThatThrownBy(() -> Coupon.createFrom(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("할인금액이 필요합니다.");
    }

    @Test
    void 유효하지_않은_할인타입인_경우_예외처리(){
        // given
        CouponCreateRequest request = new CouponCreateRequest();
        request.setName("비정상 쿠폰");
        request.setTotalCount(100L);
        request.setDiscountType(null); // 잘못된 타입

        // when & then
        assertThatThrownBy(() -> Coupon.createFrom(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 할인 타입입니다.");
    }
}
