package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
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

    private Payment(Order order, Long totalAmount, String failureReason, PaymentType type){
        this.order = order;
        this.totalAmount = totalAmount;
        this.failureReason = failureReason;
        this.type = type;
    }

    public static Payment create(PaymentCreateRequest request) {
        return new Payment(
                request.getOrder(),
                request.getTotalAmount(),
                null,
                PaymentType.PENDING
        );
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
