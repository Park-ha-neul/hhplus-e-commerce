package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.UserPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderTest {

    @Test
    void 주문_등록_성공(){
        UserPoint point = mock(UserPoint.class);
        UserCoupon coupon = mock(UserCoupon.class);

        Order result = Order.create(point, coupon);

        assertEquals(0, result.getOrderItems().size());
    }

    @Test
    void 주문_완료(){
        UserPoint userPoint = mock(UserPoint.class);
        UserCoupon userCoupon = mock(UserCoupon.class);
        Order order = Order.create(userPoint, userCoupon);
        order.complete();

        assertEquals(OrderType.SUCCESS, order.getType());
    }

    @Test
    void 주문_실패(){
        UserPoint userPoint = mock(UserPoint.class);
        UserCoupon userCoupon = mock(UserCoupon.class);
        Order order = Order.create(userPoint, userCoupon);
        order.cancle();

        assertEquals(OrderType.FAIL, order.getType());
    }
}
