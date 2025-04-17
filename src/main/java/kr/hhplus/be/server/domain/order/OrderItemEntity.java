package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_item")
@Builder
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private OrderEntity orderEntity;

    @ManyToOne
    private ProductEntity productEntity;

    private Long quantity;
    private Long unitPrice;

    public OrderItemEntity(OrderEntity orderEntity, ProductEntity productEntity, Long quantity, Long price) {
        this.orderEntity = orderEntity;
        this.productEntity = productEntity;
        this.quantity = quantity;
        this.unitPrice = price;
    }

    public static OrderItemEntity create(OrderEntity orderEntity, ProductEntity productEntity, Long quantity, Long price) {
        return new OrderItemEntity(orderEntity, productEntity, quantity, price);
    }

    public Long getTotalPrice() {
        return unitPrice * quantity;
    }
}
