package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.api.coupon.CouponCreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class CouponTest {
    @Test
    void 할인타입_RATE_쿠폰_생성_성공() {
        // given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("할인쿠폰")
                .totalCount(100L)
                .discountType(Coupon.DiscountType.RATE)
                .discountRate(10L)
                .discountAmount(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // when
        Coupon coupon = Coupon.create(request);

        // then
        assertEquals("할인쿠폰", coupon.getName());
        assertEquals(Coupon.DiscountType.RATE, coupon.getDiscountType());
        assertEquals(Long.valueOf(10L), coupon.getDiscountRate());
        assertEquals(Long.valueOf(0L), coupon.getIssuedCount());
    }

    @Test
    void 할인타입_AMOUNT_쿠폰_생성_성공() {
        // given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("정액쿠폰")
                .totalCount(50L)
                .discountType(Coupon.DiscountType.AMOUNT)
                .discountRate(null)
                .discountAmount(3000L)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        // when
        Coupon coupon = Coupon.create(request);

        // then
        assertEquals(Coupon.DiscountType.AMOUNT, coupon.getDiscountType());
        assertEquals(Long.valueOf(3000L), coupon.getDiscountAmount());
    }

    @Test
    void 할인타입_RATE인데_할인율_null이면_예외() {
        // given
        CouponCreateRequest request = CouponCreateRequest.builder()
                .name("예외쿠폰")
                .totalCount(10L)
                .discountType(Coupon.DiscountType.RATE)
                .discountRate(null)
                .discountAmount(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        // expect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Coupon.create(request);
        });

        assertEquals(ErrorCode.DISCOUNT_RATE_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void 발급가능_수량보다_작으면_true반환() {
        Coupon coupon = Coupon.builder()
                .name("발급쿠폰")
                .totalCount(100L)
                .issuedCount(99L)
                .discountType(Coupon.DiscountType.AMOUNT)
                .discountRate(null)
                .discountAmount(1000L)
                .status(Coupon.CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        assertTrue(coupon.isIssuable());
    }

    @Test
    void 발급수량_초과시_increaseIssuedCount_예외() {
        Coupon coupon = Coupon.builder()
                .name("초과쿠폰")
                .totalCount(2L)
                .issuedCount(2L)
                .discountType(Coupon.DiscountType.AMOUNT)
                .discountRate(null)
                .discountAmount(1000L)
                .status(Coupon.CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, coupon::increaseIssuedCount);
        assertEquals(ErrorCode.COUPON_ISSUED_EXCEED.getMessage(), exception.getMessage()); // ErrorCode.COUPON_ISSUED_EXCEED.getMessage()
    }

    @Test
    void 할인타입이_RATE일_경우_올바르게_할인계산() {
        Coupon coupon = Coupon.builder()
                .name("10% 할인")
                .totalCount(100L)
                .issuedCount(0L)
                .discountType(Coupon.DiscountType.RATE)
                .discountRate(10L)
                .discountAmount(null)
                .status(Coupon.CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        long discount = coupon.calculateDiscount(5000L);

        assertEquals(500L, discount); // 10% of 5000
    }

    @Test
    void 할인타입이_AMOUNT일_경우_정액할인() {
        Coupon coupon = Coupon.builder()
                .name("3000원 할인")
                .totalCount(100L)
                .issuedCount(0L)
                .discountType(Coupon.DiscountType.AMOUNT)
                .discountRate(null)
                .discountAmount(3000L)
                .status(Coupon.CouponStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();

        long discount = coupon.calculateDiscount(10000L);

        assertEquals(3000L, discount);
    }
}
