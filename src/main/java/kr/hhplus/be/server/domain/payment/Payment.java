package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long totalAmount;
    private String failureReason;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    public void setPending(){
        this.type = PaymentType.PENDING;
    }

    public void complete(){
        this.type = PaymentType.COMPLETED;
    }

    public void fail(String reason){
        this.type = PaymentType.FAIL;
        this.failureReason = reason;
    }

    public boolean isCompleted(){
        return this.type == PaymentType.COMPLETED;
    }
}
