package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseEntity;
import lombok.Getter;

@Entity
@Getter
@Table(name = "payment")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "total_amount")
    private Long totalAmount;

    @Column(name = "description")
    private String failureReason;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus type;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAIL
    }

    public Payment(Long orderId, Long totalAmount){
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.type = PaymentStatus.PENDING;
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
