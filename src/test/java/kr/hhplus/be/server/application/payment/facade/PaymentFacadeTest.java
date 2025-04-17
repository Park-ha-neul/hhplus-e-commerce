package kr.hhplus.be.server.application.payment.facade;

import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderItemEntity;
import kr.hhplus.be.server.domain.payment.ErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentEntityRepository;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeTest {

    @Mock
    private PaymentEntityRepository paymentEntityRepository;

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Test
    void 결제_성공() {
        // given
        Long paymentId = 1L;
        PaymentEntity paymentEntity = mock(PaymentEntity.class);
        OrderEntity orderEntity = mock(OrderEntity.class);
        OrderItemEntity orderItemEntity1 = mock(OrderItemEntity.class);
        OrderItemEntity orderItemEntity2 = mock(OrderItemEntity.class);
        UserCouponEntity userCouponEntity = mock(UserCouponEntity.class);
        UserPointEntity userPointEntity = mock(UserPointEntity.class);
        UserPoint userPoint = mock(UserPoint.class);
        CouponEntity couponEntity = mock(CouponEntity.class);

        when(paymentEntityRepository.findById(paymentId)).thenReturn(Optional.of(paymentEntity));
        when(paymentEntity.getOrderEntity()).thenReturn(orderEntity);
        when(orderEntity.getOrderItemEntities()).thenReturn(List.of(orderItemEntity1, orderItemEntity2));
        when(orderItemEntity1.getTotalPrice()).thenReturn(500L);
        when(orderItemEntity2.getTotalPrice()).thenReturn(500L);

        when(orderEntity.getUserCouponEntity()).thenReturn(userCouponEntity);
        when(orderEntity.getUserPointEntity()).thenReturn(userPointEntity);
        when(orderEntity.getUserPointEntity().getPoint()).thenReturn(userPoint);

        when(userCouponEntity.getCouponEntity()).thenReturn(couponEntity);
        when(couponEntity.calculateDiscount(1000L)).thenReturn(100L);

        doNothing().when(userPoint).use(900L); // finalAmount = 1000 - 100 = 900

        // when
        PaymentEntity result = paymentFacade.completePayment(paymentId);

        // then
        verify(paymentEntityRepository).findById(paymentId);
        verify(paymentEntityRepository).save(paymentEntity);
        verify(paymentEntity).complete();
        verify(orderEntity).complete();
        verify(userPoint).use(900L);
        verify(userCouponEntity).use();
    }

    @Test
    void 결제_실패() {
        // given
        Long paymentId = 1L;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentFacade.completePayment(paymentId);
        });

        // ErrorCode.PAYMENT_NOT_FOUND의 메시지가 예외 메시지와 일치하는지 검증
        assertEquals(ErrorCode.PAYMENT_NOT_FOUND.getMessage(), exception.getMessage());
    }
}
