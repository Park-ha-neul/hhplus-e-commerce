package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_description")
    private String description;

    @Column(name = "price")
    private Long price;

    @Column(name = "stock")
    private Long quantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    public enum ProductStatus {
        AVAILABLE, SOLD_OUT
    }

    protected Product(){
    }

    @Builder
    public Product(Long productId, String name, String description, Long price, Long quantity, ProductStatus status){
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    public static Product create(ProductCommand command){
        if (command.getPrice() < 0){
            throw new IllegalArgumentException(ProductErrorCode.PRODUCT_PRICE_MUST_BE_POSITIVE.getMessage());
        }

        if (command.getQuantity() == null || command.getQuantity() == null || command.getQuantity() < 0){
            throw new IllegalArgumentException(ProductErrorCode.PRODUCT_STOCK_MUST_BE_POSITIVE.getMessage());
        }

        return new Product(
                null,
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getQuantity(),
                ProductStatus.AVAILABLE
        );
    }

    public void increaseBalance(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException(ProductErrorCode.INCREASE_MUST_BE_POSITIVE.getMessage());
        }
        this.quantity += amount;


        if (!this.isSoldOut()) {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    public void decreaseBalance(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException(ProductErrorCode.DECREASE_MUST_BE_POSITIVE.getMessage());
        }
        if(amount > quantity){
            throw new IllegalArgumentException(ProductErrorCode.NOT_ENOUGH_STOCK.getMessage());
        }
        this.quantity -= amount;

        if (this.isSoldOut()) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public boolean isSoldOut(){
        return this.quantity == 0L;
    }
}
