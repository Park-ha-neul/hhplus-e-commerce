package kr.hhplus.be.server.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "📌 결제 관리", description = "결제 관련 API 모음")
public class PaymentController {
    @PostMapping("/")
    @Operation(summary = "결제 생성", description = "결제 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "결제 등록 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "paymentId": 1,
                                "odreId": 1,
                                "totalAmount": 1000,
                                "status": "pending",
                                "description": "",
                                "createDate": "2024-04-02T12:00:00Z"
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "사용자 id가 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "상품 id가 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "결제 금액이 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "결제 금액이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "주문 id가 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "주문 id가 누락되었습니다. 필수 값을 확인해주세요."
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
    public CustomApiResponse createPayment(
            @RequestBody
            @Schema(description = "결제 정보",
                    example = "{\"userId\": 123, \"amount\": 1000, \"orderId\": 1}")
            Map<String,Object> paymentRequest
    ){
        Map<String, Object> paymentData = new LinkedHashMap<>();
        paymentData.put("paymentId", 1);
        paymentData.put("odreId", 1);
        paymentData.put("totalAmount", 1000);
        paymentData.put("status", "pending");
        paymentData.put("description", "");
        paymentData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(paymentData);
    }

    @PatchMapping("/{paymentId}")
    @Operation(summary = "결제 진행", description = "결제를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "결제 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "paymentId": 1,
                                "odreId": 1,
                                "totalAmount": 1000,
                                "status": "success",
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:00:00Z",
                                "products": [
                                    {"productId": 1, "amount": 2},
                                    {"productId": 2, "amount": 3}
                                ]
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "결제 id 오류", value = """
                        {
                            "code": "400",
                            "message": "정의되지 않은 결제 id 입니다."
                        }
                        """),
                    @ExampleObject(name = "잔액 부족", value = """
                        {
                            "code": "400",
                            "message": "잔액이 부족합니다. 충전 후 다시 이용해주세요."
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
    public CustomApiResponse proccessPayment(@PathVariable("paymentId") @Parameter(name = "paymentId", description = "결제 id")Long paymentId){
        Map<String, Object> paymentData = new LinkedHashMap<>();

        List<Map<String, Object>> productDataList = new ArrayList<>();
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", 1);
        productData.put("amount", 2);

        Map<String, Object> productData2 = new HashMap<>();
        productData2.put("productId", 2);
        productData2.put("amount", 3);
        productDataList.add(productData);
        productDataList.add(productData2);


        paymentData.put("paymentId", 1);
        paymentData.put("odreId", 1);
        paymentData.put("totalAmount", 1000);
        paymentData.put("status", "success");
        paymentData.put("description", "");
        paymentData.put("createDate", "2024-04-02T12:00:00Z");
        paymentData.put("updateDate", "2024-04-02T12:00:00Z");
        paymentData.put("products", productDataList);

        return CustomApiResponse.success(paymentData);
    }

    @GetMapping("/")
    @Operation(summary = "결제 목록 조회", description = "결제 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                "paymentId": 1,
                                "odreId": 1,
                                "totalAmount": 1000,
                                "status": "success",
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:00:00Z",
                                "products": [
                                    {"productId": 1, "amount": 2},
                                    {"productId": 2, "amount": 3}
                                ]},{}...{}
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
    public CustomApiResponse getPayments(){
        List<Map<String, Object>> paymentDataList = new ArrayList<>();
        Map<String, Object> paymentData = new LinkedHashMap<>();

        List<Map<String, Object>> productDataList = new ArrayList<>();
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", 1);
        productData.put("amount", 2);

        Map<String, Object> productData2 = new HashMap<>();
        productData2.put("productId", 2);
        productData2.put("amount", 3);
        productDataList.add(productData);
        productDataList.add(productData2);


        paymentData.put("paymentId", 1);
        paymentData.put("odreId", 1);
        paymentData.put("totalAmount", 1000);
        paymentData.put("status", "success");
        paymentData.put("description", "");
        paymentData.put("createDate", "2024-04-02T12:00:00Z");
        paymentData.put("updateDate", "2024-04-02T12:00:00Z");
        paymentData.put("products", productDataList);

        Map<String, Object> paymentData2 = new LinkedHashMap<>();

        paymentData2.put("paymentId", 2);
        paymentData2.put("odreId", 2);
        paymentData2.put("totalAmount", 2000);
        paymentData2.put("status", "success");
        paymentData2.put("description", "");
        paymentData2.put("createDate", "2024-04-02T12:00:00Z");
        paymentData2.put("updateDate", "2024-04-02T12:00:00Z");
        paymentData2.put("products", productDataList);

        paymentDataList.add(paymentData);
        paymentDataList.add(paymentData2);

        return CustomApiResponse.success(paymentDataList);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 상세 조회", description = "결제 상세 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                "paymentId": 1,
                                "odreId": 1,
                                "totalAmount": 1000,
                                "status": "success",
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:00:00Z",
                                "products": [
                                    {"productId": 1, "amount": 2},
                                    {"productId": 2, "amount": 3}
                            ]}
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 결제 id 입니다."
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
    public CustomApiResponse getPaymentDetail(@PathVariable("paymentId") @Parameter(name = "paymentId", description = "결제 id")Long paymentId){
        List<Map<String, Object>> paymentDataList = new ArrayList<>();
        Map<String, Object> paymentData = new LinkedHashMap<>();

        List<Map<String, Object>> productDataList = new ArrayList<>();
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", 1);
        productData.put("amount", 2);

        Map<String, Object> productData2 = new HashMap<>();
        productData2.put("productId", 2);
        productData2.put("amount", 3);
        productDataList.add(productData);
        productDataList.add(productData2);

        paymentData.put("paymentId", paymentId);
        paymentData.put("odreId", 1);
        paymentData.put("totalAmount", 1000);
        paymentData.put("status", "success");
        paymentData.put("description", "");
        paymentData.put("createDate", "2024-04-02T12:00:00Z");
        paymentData.put("updateDate", "2024-04-02T12:00:00Z");
        paymentData.put("products", productDataList);

        paymentDataList.add(paymentData);

        return CustomApiResponse.success(paymentDataList);
    }
}
