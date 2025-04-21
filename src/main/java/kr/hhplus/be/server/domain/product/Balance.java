package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
public class Balance {
    @Column(name = "stock")
    private Long quantity;

    Balance(Long quantity){
        this.quantity = quantity;
    }

    public static Balance create(Long quantity){
        return new Balance(quantity);
    }

    public Long getQuantity(){
        return this.quantity;
    }

    public void increase(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException(ProductErrorCode.INCREASE_MUST_BE_POSITIVE.getMessage());
        }
        this.quantity += amount;
    }

    public void decrease(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException(ProductErrorCode.DECREASE_MUST_BE_POSITIVE.getMessage());
        }
        if(amount > quantity){
            throw new IllegalArgumentException(ProductErrorCode.NOT_ENOUGH_STOCK.getMessage());
        }
        this.quantity -= amount;
    }

    public boolean isSoldOut(){
        return this.quantity == 0L;
    }
}
