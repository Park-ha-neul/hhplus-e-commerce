package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.facade.OrderFacade;
import kr.hhplus.be.server.application.facade.OrderFacadeRequest;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
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

    private final OrderFacade orderFacade;
    private final OrderService orderService;

    @PostMapping("/")
    @Operation(summary = "주문 등록", description = "주문을 등록합니다.")
    public CustomApiResponse create(@RequestBody OrderRequest request){
        try{
            OrderFacadeRequest orderFacadeRequest = request.toRequest();
            Order order = orderFacade.order(orderFacadeRequest);
            OrderResponse response = OrderResponse.fromOrder(order);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, response);
        } catch(IllegalArgumentException e){
            return CustomApiResponse.badRequest(e.getMessage());
        } catch(Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    }

    @GetMapping("/")
    @Operation(summary = "주문 목록 조회", description = "상태에 따른 주문 목록 조회가 가능합니다.")
    public CustomApiResponse getOrders(
            @RequestParam(value = "status", required = false) Order.OrderStatus status
    ){
        List<Order> data = orderService.getOrders(status);
        List<OrderResponse> response = OrderResponse.fromOrderList(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @GetMapping("/{order_id}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세 내용을 조회합니다.")
    public CustomApiResponse getOrder(
            @PathVariable("order_id") @Parameter(name = "order_id", description = "주문 ID") Long orderId
    ){
        Order data = orderService.getOrder(orderId);
        OrderResponse response = OrderResponse.fromOrder(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @DeleteMapping("/{order_id}")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    public CustomApiResponse delete(
            @PathVariable("order_id") @Parameter(name = "order_id", description = "주문 ID") Long orderId
    ){
        orderService.cancel(orderId);
        return CustomApiResponse.success(ApiMessage.CANCLE_SUCCESS);
    }
}
