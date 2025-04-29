package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_item")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "unit_price")
    private Long unitPrice;

    public OrderItem(){
    }

    @Builder
    public OrderItem(Order order, Long productId, Long quantity, Long price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = price;
    }

    public Long getTotalPrice() {
        return unitPrice * quantity;
    }
}
