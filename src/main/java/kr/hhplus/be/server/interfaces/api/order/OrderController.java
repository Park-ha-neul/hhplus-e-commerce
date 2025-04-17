package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "📌 주문 관리", description = "주문 관련 API 모음")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    @Operation(summary = "주문 등록", description = "주문을 등록합니다.")
    public CustomApiResponse createOrder(@RequestBody OrderReqeust request){
        try{
            OrderEntity result = orderService.createOrder(request);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, result);
        } catch(IllegalArgumentException e){
            return CustomApiResponse.badRequest(e.getMessage());
        } catch(Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    }

    @GetMapping("/")
    @Operation(summary = "주문 목록 조회", description = "상태에 따른 주문 목록 조회가 가능합니다.")
    public CustomApiResponse getOrders(
            @RequestParam(required = false) OrderStatus status
    ){
        List<OrderEntity> data = orderService.getOrders(status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @GetMapping("/{order_id}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 내용을 조회합니다.")
    public CustomApiResponse getOrder(
            @PathVariable("orderId") @Parameter(name = "orderId", description = "주문 ID") Long orderId
    ){
        OrderEntity data = orderService.getOrder(orderId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }
}
