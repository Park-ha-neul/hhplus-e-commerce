package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product extends BaseEntity {
    @Id
    private Long productId;

    private String name;
    private String description;
    private Long price;

    @OneToOne
    private Balance balance;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    public void updateStatus(ProductStatus status){
        this.status = status;
    }
}
