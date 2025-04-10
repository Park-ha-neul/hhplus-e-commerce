package kr.hhplus.be.server.application.service.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemDto;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.product.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void createOrderItem(OrderItemDto orderItemDto, Order order) {
        // 1. 상품을 조회하고 재고를 확인
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. 재고 차감
        Balance balance = product.getBalance();
        balance.decrease(orderItemDto.getQuantity());

        // 3. 주문 항목 저장
        OrderItem orderItem = new OrderItem(null, order, product, orderItemDto.getQuantity(), orderItemDto.getUnitPrice());
        orderItemRepository.save(orderItem);
    }
}
