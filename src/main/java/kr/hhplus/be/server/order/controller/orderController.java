package kr.hhplus.be.server.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.common.CustomApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Tag(name = "📌 주문 관리", description = "주문 관련 API 모음")
public class orderController {
    @GetMapping("/")
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                "orderId": 1,
                                "userId": 1,
                                "status": "pending",
                                "createDate": "2024-04-02T12:00:00Z"
                               },{}...]
                        }
                        """),
            })),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "오류 - 서버 에러", value = """
                        {
                            "code": "500",
                             "message": "서버 에러"
                        }
                        """)
            }))
    })
    public CustomApiResponse getOrders(){
        List<Map<String, Object>> orderList = new ArrayList<>();

        Map<String, Object> odrderData = new LinkedHashMap<>();
        odrderData.put("orderId", 1);
        odrderData.put("userId", 1);
        odrderData.put("status", "pending");
        odrderData.put("createDate", "2024-04-02T12:00:00Z");

        Map<String, Object> odrderData1 = new LinkedHashMap<>();
        odrderData1.put("orderId", 1);
        odrderData1.put("userId", 1);
        odrderData1.put("status", "pending");
        odrderData1.put("createDate", "2024-04-02T12:00:00Z");

        orderList.add(odrderData);
        orderList.add(odrderData1);

        return CustomApiResponse.success(orderList);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "orderId": 1,
                                "userId": 1,
                                "status": "pending",
                                "createDate": "2024-04-02T12:00:00Z"
                               }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 주문입니다."
                        }
                        """)
            })),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "오류 - 서버 에러", value = """
                        {
                            "code": "500",
                             "message": "서버 에러"
                        }
                        """)
            }))
    })
    public CustomApiResponse getOrderDetail(@PathVariable("orderId") @Parameter(name = "orderId", description = "주문 id")Long orderId){
        Map<String, Object> odrderData = new LinkedHashMap<>();
        odrderData.put("orderId", orderId);
        odrderData.put("userId", 1);
        odrderData.put("status", "pending");
        odrderData.put("createDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(odrderData);
    }

    @PostMapping("/")
    @Operation(summary = "주문 등록", description = "주문을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "주문 등록 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "orderId": 1,
                                "userId": 1,
                                "status": "pending",
                                "createDate": "2024-04-02T12:00:00Z"
                               }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품 목록이 비어있는 경우", value = """
                        {
                            "code": "400",
                             "message": "상품 목록이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "상품 ID가 유효하지 않은 경우", value = """
                        {
                            "code": "400",
                             "message": "상품 ID가 유효하지 않습니다."
                        }
                        """),
                    @ExampleObject(name = "상품 재고가 부족한 경우", value = """
                        {
                            "code": "400",
                             "message": "상품 재고가 부족합니다."
                        }
                        """),
                    @ExampleObject(name = "쿠폰이 유효하지 않은 경우", value = """
                        {
                            "code": "400",
                             "message": "쿠폰이 유효하지 않습니다."
                        }
                        """)
            })),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "오류 - 서버 에러", value = """
                        {
                            "code": "500",
                             "message": "서버 에러"
                        }
                        """)
            }))
    })
    public CustomApiResponse createOrder(
            @RequestBody
            @Schema(description = "주문 정보",
                    example = "{\"userId\": 1, \"couponId\": 2, \"orderItems\": [{\"productId\": 1, \"quantity\": 1}, {\"productId\": 2, \"quantity\": 2}]}")
            Map<String,Object> orderRequest
    ){
        Map<String, Object> odrderData = new LinkedHashMap<>();
        odrderData.put("orderId", 1);
        odrderData.put("userId", 1);
        odrderData.put("status", "pending");
        odrderData.put("createDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(odrderData);
    }
}
