package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.product.ProductEntity;
import kr.hhplus.be.server.domain.product.ProductEntityRepository;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.user.UserPoint;
import kr.hhplus.be.server.domain.user.UserPointEntity;
import kr.hhplus.be.server.domain.user.UserPointEntityRepository;
import kr.hhplus.be.server.domain.user.UserPointErrorCode;
import kr.hhplus.be.server.interfaces.api.order.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.OrderReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private OrderEntityRepository orderEntityRepository;
    private UserPointEntityRepository userPointEntityRepository;
    private UserCouponEntityRepository userCouponEntityRepository;
    private ProductEntityRepository productEntityRepository;

    public OrderEntity createOrder(OrderReqeust request){
        UserPointEntity userPointEntity = userPointEntityRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(UserPointErrorCode.USER_NOT_FOUND.getMessage()));

        UserCouponEntity userCouponEntity = userCouponEntityRepository.findByUserAndCoupon(request.getUserId(), request.getCouponId());

        OrderEntity orderEntity = OrderEntity.create(userPointEntity, userCouponEntity);

        for (OrderItemRequest orderItemRequest : request.getOrderItems()) {
            ProductEntity productEntity = productEntityRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage()));
            OrderItemEntity orderItemEntity = OrderItemEntity.create(orderEntity, productEntity, orderItemRequest.getQuantity(), productEntity.getPrice());
            orderEntity.addOrderItem(orderItemEntity);
        }

        orderEntityRepository.save(orderEntity);
        orderEntity.complete();
        return orderEntity;
    }

    public List<OrderEntity> getOrders(OrderStatus status){
        List<OrderEntity> orders = orderEntityRepository.findByStatus(status);
        return orders;

    }

    public OrderEntity getOrder(Long orderId){
        return orderEntityRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(OrderErrorCode.ORDER_NOT_FOUND.getMessage()));
    }

    public List<OrderEntity> getOrderByUserId(Long userId) {
        List<OrderEntity> orderEntities = orderEntityRepository.findByUserId(userId);
        return orderEntities;
    }

    public void cancleOrder(Long orderId){
        OrderEntity orderEntity = getOrder(orderId);
        orderEntity.cancel();
        orderEntityRepository.save(orderEntity);
    }
}
