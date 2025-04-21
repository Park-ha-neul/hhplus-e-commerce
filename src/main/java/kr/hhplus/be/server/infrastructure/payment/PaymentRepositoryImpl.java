package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return paymentJpaRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> findAllByStatus(Payment.PaymentStatus status) {
        return paymentJpaRepository.findAllByStatus(status);
    }

    @Override
    public List<Payment> findAllPayments() {
        return paymentJpaRepository.findAll();
    }
}
