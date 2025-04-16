package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItem(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());

        if(items.isEmpty()){
            throw new IllegalArgumentException(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
        }

        return items;
    }

    public OrderItem createOrderItem(Order order, Product product, Long quantity, Long price){
        OrderItem result = OrderItem.create(order, product, quantity, price);
        return orderItemRepository.save(result);
    }
}