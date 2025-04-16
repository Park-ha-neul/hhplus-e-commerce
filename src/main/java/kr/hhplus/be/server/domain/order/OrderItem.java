package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long quantity;
    private Long unitPrice;

    public OrderItem(Order order, Product product, Long quantity, Long price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = price;
    }

    public static OrderItem create(Order order, Product product, Long quantity, Long price) {
        return new OrderItem(order, product, quantity, price);
    }

    public Long getTotalPrice() {
        return unitPrice * quantity;
    }
}
