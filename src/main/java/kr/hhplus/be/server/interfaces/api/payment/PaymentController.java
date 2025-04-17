package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.payment.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
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
    private final PaymentFacade paymentFacade;

    @PostMapping("/")
    @Operation(summary = "결제 생성", description = "결제 생성합니다.")
    public CustomApiResponse createPayment(@RequestBody PaymentCreateRequest request){
        try{
            PaymentEntity result = paymentService.create(request);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, result);
        } catch(IllegalArgumentException e){
            return CustomApiResponse.badRequest(e.getMessage());
        }catch(Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    }

    @PostMapping("/{paymentId}")
    @Operation(summary = "결제 진행", description = "결제를 진행합니다.")
    public CustomApiResponse processPayment(@PathVariable("paymentId") @Parameter(name = "paymentId", description = "결제 ID") Long paymentId){
        PaymentEntity data = paymentFacade.completePayment(paymentId);
        return CustomApiResponse.success(ApiMessage.PAYMENT_SUCCESS);
    }

    @GetMapping("/")
    @Operation(summary = "결제 목록 조회", description = "결제 상태에 따른 조회가 가능합니다.")
    public CustomApiResponse getPayments(
            @RequestParam(required = false)PaymentStatus status
    ){
        List<PaymentEntity> data = paymentService.getPayments(status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 목록 조회", description = "결제 상태에 따른 조회가 가능합니다.")
    public CustomApiResponse getPayment(
            @PathVariable("paymentId") @Parameter(name = "paymentId", description = "결제 id") Long paymentId
    ){
        PaymentEntity data = paymentService.getPayment(paymentId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }
}
