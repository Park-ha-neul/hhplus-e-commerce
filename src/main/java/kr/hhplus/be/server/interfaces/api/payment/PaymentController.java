package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.facade.ProcessPayment;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentResult;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "📌 결제 관리", description = "결제 관련 API 모음")
public class PaymentController {

    private final PaymentService paymentService;
    private final ProcessPayment processPayment;

    @PostMapping("/")
    @Operation(summary = "결제 생성", description = "결제 생성합니다.")
    public CustomApiResponse createPayment(@RequestBody PaymentCreateRequest request){
        try{
            PaymentCommand command = request.toCommand();
            PaymentResult result = paymentService.create(command);
            PaymentResponse response = PaymentResponse.from(result);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, response);
        } catch(IllegalArgumentException e){
            return CustomApiResponse.badRequest(e.getMessage());
        }catch(Exception e){
            System.out.println("e : " + e.getMessage());
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    }

    @PostMapping("/{payment_id}")
    @Operation(summary = "결제 진행", description = "결제를 진행합니다.")
    public CustomApiResponse processPayment(@PathVariable("payment_id") @Parameter(name = "paymentId", description = "결제 ID") Long paymentId){
        Payment data = processPayment.processPayment(paymentId);
        return CustomApiResponse.success(ApiMessage.PAYMENT_SUCCESS);
    }

    @GetMapping("/")
    @Operation(summary = "결제 목록 조회", description = "결제 상태에 따른 조회가 가능합니다.")
    public CustomApiResponse getPayments(
            @RequestParam(value = "status", required = false)Payment.PaymentStatus status
    ){
        List<Payment> data = paymentService.getPayments(status);
        List<PaymentResponse> response = PaymentResponse.fromPaymentList(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @GetMapping("/{payment_id}")
    @Operation(summary = "결제 상세 조회", description = "결제 상태에 따른 조회가 가능합니다.")
    public CustomApiResponse getPayment(
            @PathVariable("payment_id") @Parameter(name = "paymentId", description = "결제 id") Long paymentId
    ){
        Payment data = paymentService.getPayment(paymentId);
        PaymentResponse response = PaymentResponse.fromPayment(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }
}
