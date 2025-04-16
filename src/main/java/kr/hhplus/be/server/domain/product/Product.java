package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;

@Entity
@Getter
public class Product extends BaseEntity {
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

    private Product(Long productId, String name, String description, Long price, Balance balance, ProductStatus status){
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.balance = balance;
        this.status = status;
    }

    public static Product create(String name, String description, Long price, Balance balance, ProductStatus status){
        if (price < 0){
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }

        if (balance == null || balance.getQuantity() == null || balance.getQuantity() < 0){
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다.");
        }

        if (balance.isSoldOut() && status == ProductStatus.AVAILABLE) {
            throw new IllegalArgumentException("재고가 없는 상품은 판매 상태일 수 없습니다.");
        }

        return new Product(
                null,
                name,
                description,
                price,
                balance,
                status
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
            this.status = ProductStatus.AVAILABLE;
        }
    }
}
