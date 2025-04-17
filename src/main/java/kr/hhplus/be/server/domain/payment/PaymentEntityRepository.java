package kr.hhplus.be.server.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByUserId(Long userId);
    List<PaymentEntity> findAllByStatus(PaymentStatus status);
}
