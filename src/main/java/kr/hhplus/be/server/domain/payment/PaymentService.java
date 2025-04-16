package kr.hhplus.be.server.domain.payment;

// 결제 생성은 usecase에 왜냐면 orderservice를 호출해야 함
// 결제 완료 후 상위 상품에 넣는것도 usecase
// 파사드에 주문, 결제 넣기

import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private PaymentRepository paymentRepository;

    public Payment getPayment(Long paymentId){
        return paymentRepository.findById(paymentId)
                .orElseThrow(()-> new IllegalArgumentException(ErrorCode.PAYMENT_NOT_FOUND.getMessage()));
    }

    public Payment create(PaymentCreateRequest request){
        Payment payment = Payment.create(request);
        return paymentRepository.save(payment);
    }

    public void updateStatusComplete(Long paymentId){
        Payment payment = getPayment(paymentId);
        payment.complete();
        paymentRepository.save(payment);
    }

    public void updateStatusFail(Long paymentId, String reason){
        Payment payment = getPayment(paymentId);
        payment.fail(reason);
        paymentRepository.save(payment);
    }
}
