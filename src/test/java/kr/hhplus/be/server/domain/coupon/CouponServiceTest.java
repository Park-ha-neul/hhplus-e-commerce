package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_등록(){
        CouponCreateRequest request = mock(CouponCreateRequest.class);

        when(request.getDiscountType()).thenReturn(DiscountType.AMOUNT);
        when(request.getDiscountAmount()).thenReturn(1000L);
        when(request.getName()).thenReturn("테스트쿠폰");
        when(request.getTotalCount()).thenReturn(100L);
        when(request.getStartDate()).thenReturn(LocalDate.now().atStartOfDay());
        when(request.getEndDate()).thenReturn(LocalDate.now().plusDays(7).atStartOfDay());

        Coupon coupon = Coupon.create(request);

        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon result = couponService.create(request);

        assertNotNull(result);
        verify(couponRepository).save(any(Coupon.class));
    }
}
