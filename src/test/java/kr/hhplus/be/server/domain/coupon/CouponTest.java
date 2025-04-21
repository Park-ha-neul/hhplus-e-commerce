package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CouponTest {
    @Test
    public void 발급_가능한_경우_true_반환(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(100L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);

        assertTrue(coupon.isIssuable());
    }

    @Test
    public void 발급_한도_초과한_경우_false_반환(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(1L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);
        coupon.increaseIssuedCount();

        assertFalse(coupon.isIssuable());
    }

    @Test
    public void 쿠폰_발급시_count_증가(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(1L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);
        coupon.increaseIssuedCount();

        assertEquals(Long.valueOf(1L), coupon.getIssuedCount());
    }

    @Test
    public void 쿠폰_발급_한도_초과시_예외_발생(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(1L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);
        coupon.increaseIssuedCount();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            coupon.increaseIssuedCount();
        });

        assertEquals(ErrorCode.COUPON_ISSUED_EXCEED.getMessage(), e.getMessage());

    }

    @Test
    void rate_타입_쿠폰으로_할인금액_계산() {
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.RATE);
        when(request.getDiscountRate()).thenReturn(10L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(1L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);

        long originalAmount = 20000L;

        long discount = coupon.calculateDiscount(originalAmount);

        assertEquals(2000L, discount);  // 10% of 20000
    }

    @Test
    void amount_타입_쿠폰으로_할인금액_계산() {
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(3000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(1L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);

        long originalAmount = 20000L;

        long discount = coupon.calculateDiscount(originalAmount);

        assertEquals(3000L, discount);
    }

    @Test
    void 쿠폰_등록_성공(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(100L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);

        assertEquals(Coupon.DiscountType.AMOUNT, coupon.getDiscountType());
    }

    @Test
    void 쿠폰_등록시_할인율이_없는경우_예외처리(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.RATE);
        when(request.getDiscountRate()).thenReturn(null);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Coupon.create(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RATE_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    void 쿠폰_등록시_할인금액이_없는경우_예외처리(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(Coupon.DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(null);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Coupon.create(request);
        });

        assertEquals(ErrorCode.DISCOUNT_AMOUNT_NOT_FOUND.getMessage(), e.getMessage());
    }

    @Test
    void 지원하지않는_할인타입_예외처리() {
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(null);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Coupon.create(request);
        });

        assertEquals(ErrorCode.COUPON_TYPE_NOT_FOUND.getMessage(), e.getMessage());
    }
}
