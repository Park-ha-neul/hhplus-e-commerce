package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product")
@Builder
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private String description;
    private Long price;

    @Embedded
    @Getter
    private Balance balance;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private ProductEntity(Long productId, String name, String description, Long price, Balance balance, ProductStatus status){
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.balance = balance;
        this.status = status;
    }

    public static ProductEntity create(ProductRequest request){
        if (request.getPrice() < 0){
            throw new IllegalArgumentException(ProductErrorCode.PRODUCT_PRICE_MUST_BE_POSITIVE.getMessage());
        }

        if (request.getBalance() == null || request.getBalance().getQuantity() == null || request.getBalance().getQuantity() < 0){
            throw new IllegalArgumentException(ProductErrorCode.PRODUCT_STOCK_MUST_BE_POSITIVE.getMessage());
        }

        return new ProductEntity(
                null,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getBalance(),
                ProductStatus.AVAILABLE
        );
    }

    public void increaseBalance(Long productId, Long amount){
        balance.increase(amount);

        if (!this.balance.isSoldOut()) {
            this.status = ProductStatus.AVAILABLE;
        }
    }

    public void decreaseBalance(Long productId, Long amount){
        balance.decrease(amount);

        if (this.balance.isSoldOut()) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }
}
