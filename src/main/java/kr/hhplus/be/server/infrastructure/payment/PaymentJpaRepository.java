package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);
    Payment findByOrderIdAndStatus(Long orderId, Payment.PaymentStatus status);
    List<Payment> findAllByStatus(Payment.PaymentStatus status);
}
