package kr.hhplus.be.server.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository{
    Payment save(Payment payment);
    Optional<Payment> findById(Long paymentId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findAllByStatus(Payment.PaymentStatus status);
    List<Payment> findAllPayments();
}
