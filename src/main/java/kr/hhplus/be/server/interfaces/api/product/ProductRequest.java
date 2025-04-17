package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.Balance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private Long price;
    private Balance balance;

    public ProductRequest(String name, String description, long price, Balance balance) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.balance = balance;
    }
}
