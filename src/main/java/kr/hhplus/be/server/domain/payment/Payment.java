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
    private PaymentStatus status;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAIL
    }

    public Payment(Long orderId, Long totalAmount){
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = PaymentStatus.PENDING;
    }

    public void complete(){
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail(String reason){
        this.status = PaymentStatus.FAIL;
        this.failureReason = reason;
    }

    public boolean isCompleted(){
        return this.status == PaymentStatus.COMPLETED;
    }
}
