package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import kr.hhplus.be.server.domain.order.OrderEntity;
import lombok.Getter;

@Entity
@Getter
@Table(name = "payment")
public class PaymentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne
    private OrderEntity orderEntity;

    private Long totalAmount;
    private String failureReason;

    @Enumerated(EnumType.STRING)
    private PaymentStatus type;

    private PaymentEntity(OrderEntity orderEntity, Long totalAmount, String failureReason, PaymentStatus type){
        this.orderEntity = orderEntity;
        this.totalAmount = totalAmount;
        this.failureReason = failureReason;
        this.type = type;
    }

    public static PaymentEntity create(OrderEntity orderEntity, Long totalAmount) {
        return new PaymentEntity(
                orderEntity,
                totalAmount,
                null,
                PaymentStatus.PENDING
        );
    }

    public void complete(){
        this.type = PaymentStatus.COMPLETED;
    }

    public void fail(String reason){
        this.type = PaymentStatus.FAIL;
        this.failureReason = reason;
    }

    public boolean isCompleted(){
        return this.type == PaymentStatus.COMPLETED;
    }
}
