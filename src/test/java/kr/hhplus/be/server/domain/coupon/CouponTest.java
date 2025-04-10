package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;

@ExtendWith(MockitoExtension.class)
public class CouponTest {
    @Test
    public void 발급_가능한_경우_true_반환(){
        Coupon coupon = new Coupon();
        coupon.setTotalCount(10L);
        coupon.setIssuedCount(5L);

        assertTrue(coupon.isIssuable());
    }

    @Test
    public void 발급_한도_초과한_경우_false_반환(){
        Coupon coupon = new Coupon();
        coupon.setTotalCount(10L);
        coupon.setIssuedCount(10L);

        assertFalse(coupon.isIssuable());
    }

    @Test
    public void 쿠폰_발급시_count_증가(){
        Coupon coupon = new Coupon();
        coupon.setTotalCount(10L);
        coupon.setIssuedCount(2L);

        coupon.increaseIssuedCount();

        assertEquals(Long.valueOf(3L), coupon.getIssuedCount());
    }

    @Test
    public void 쿠폰_발급_한도_초과시_예외_발생(){
        Coupon coupon = new Coupon();
        coupon.setTotalCount(10L);
        coupon.setIssuedCount(10L);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            coupon.increaseIssuedCount();
        });

        assertEquals("쿠폰 발급 한도를 초과했습니다.", e.getMessage());

    }

    @Test
    void rate_타입_쿠폰으로_할인금액_계산() {
        Coupon coupon = new Coupon();
        coupon.setDiscountType(DiscountType.RATE);
        coupon.setDiscountRate(10L);  // 10% 할인

        long originalAmount = 20000L;

        long discount = coupon.calculateDiscount(originalAmount);

        assertEquals(2000L, discount);  // 10% of 20000
    }

    @Test
    void amount_타입_쿠폰으로_할인금액_계산() {
        Coupon coupon = new Coupon();
        coupon.setDiscountType(DiscountType.AMOUNT);
        coupon.setDiscountAmount(3000L);  // 3000원 할인

        long originalAmount = 20000L;

        long discount = coupon.calculateDiscount(originalAmount);

        assertEquals(3000L, discount);
    }

    @Test
    void 지원하지않는_할인타입_예외처리() {
        Coupon coupon = new Coupon();
        coupon.setDiscountType(null);  // 또는 일부러 INVALID enum을 만들어서 테스트할 수도 있음

        long originalAmount = 20000L;

        assertThrows(IllegalStateException.class, () -> {
            coupon.calculateDiscount(originalAmount);
        });
    }
}
