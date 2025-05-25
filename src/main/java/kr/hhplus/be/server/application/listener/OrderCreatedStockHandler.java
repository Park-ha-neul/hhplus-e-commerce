package kr.hhplus.be.server.application.listener;

import kr.hhplus.be.server.domain.order.OrderCreatedStockDeductEvent;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedStockHandler {

    private final ProductService productService;

    public OrderCreatedStockHandler(ProductService productService) {
        this.productService = productService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCompleted(OrderCreatedStockDeductEvent event){
        for (OrderItem item : event.getItems()) {
            productService.decreaseProductStock(item.getProductId(), item.getQuantity());
        }
    }
}
