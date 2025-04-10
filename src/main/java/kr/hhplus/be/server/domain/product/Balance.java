package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Balance {
    private Long quantity;

    public void increase(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException("증가량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }

    public void decrease(Long amount){
        if(amount == null || amount <= 0){
            throw new IllegalArgumentException("차감량은 0보다 커야 합니다.");
        }
        if(amount > quantity){
            throw new IllegalArgumentException("재고 부족");
        }
        this.quantity -= amount;
    }

    public boolean isSoldOut(){
        return this.quantity == 0;
    }
}
