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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@Tag(name = "📌 쿠폰 관리", description = "쿠폰 관련 API 모음")
public class CouponController {
    @PostMapping("/")
    @Operation(summary = "쿠폰 생성", description = "쿠폰을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰 등록 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "couponId": 1,
                                "couponName": "10% 할인 쿠폰",
                                "totalQuantity": 1000,
                                "discountRate": 10,
                                "discountAmount": null,
                                "status": "active",
                                "startDate": "2025-04-10T00:00:00Z",
                                "endDate": "2025-12-31T23:59:59Z"
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰명이 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "쿠폰명이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "총 수량이 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "쿠폰 총 수량이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "discountRate & discountAmount 둘 다 비어있는 경우", value = """
                        {
                            "code": "400",
                            "message": "할인율 or 할인 금액을 입력해주세요."
                        }
                        """),
                    @ExampleObject(name = "쿠폰 사용 기간이 없는 경우", value = """
                        {
                            "code": "400",
                            "message": "쿠폰 사용 기간이 누락되었습니다. 필수 값을 확인해주세요."
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
    public CustomApiResponse createCoupons(
            @RequestBody
            @Schema(description = "쿠폰 정보",
                    example = "{\"couponName\": \"10% 할인 쿠폰\", \"totalQuantity\": 1000, \"discountRate\": 10, \"discountAmount\": \"\", \"startDate\": \"2025-04-10T00:00:00Z\", \"endDate\": \"2025-12-31T23:59:59Z\"}")
            Map<String,Object> couponRequest
    ){
        Map<String, Object> couponData = new LinkedHashMap<>();
        couponData.put("couponId", 1);
        couponData.put("couponName", "10% 할인 쿠폰");
        couponData.put("totalQuantity", 1000);
        couponData.put("discountRate", 10);
        couponData.put("discountAmount", "");
        couponData.put("status", "active");
        couponData.put("startDate", "2025-04-10T00:00:00Z");
        couponData.put("endDate", "2025-04-10T00:00:00Z");

        return CustomApiResponse.success(couponData);
    }

    @GetMapping("/")
    @Operation(summary = "쿠폰 목록 조회", description = "쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰 목록 조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                "couponId": 1,
                                "couponName": "10% 할인 쿠폰",
                                "totalQuantity": 1000,
                                "discountRate": 10,
                                "discountAmount": null,
                                "status": "active",
                                "startDate": "2025-04-10T00:00:00Z",
                                "endDate": "2025-12-31T23:59:59Z"
                            },{}...{}]
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
    public CustomApiResponse getCoupons(){
        List<Map<String, Object>> couponDataList = new ArrayList<>();
        Map<String, Object> couponData = new LinkedHashMap<>();
        couponData.put("couponId", 1);
        couponData.put("couponName", "10% 할인 쿠폰");
        couponData.put("totalQuantity", 1000);
        couponData.put("discountRate", 10);
        couponData.put("discountAmount", "");
        couponData.put("status", "active");
        couponData.put("startDate", "2025-04-10T00:00:00Z");
        couponData.put("endDate", "2025-04-10T00:00:00Z");

        Map<String, Object> couponData2 = new LinkedHashMap<>();
        couponData2.put("couponId", 2);
        couponData2.put("couponName", "5000원 할인 쿠폰");
        couponData2.put("totalQuantity", 1000);
        couponData2.put("discountRate", "");
        couponData2.put("discountAmount", 5000);
        couponData2.put("status", "active");
        couponData2.put("startDate", "2025-04-10T00:00:00Z");
        couponData2.put("endDate", "2025-04-10T00:00:00Z");

        couponDataList.add(couponData);
        couponDataList.add(couponData2);

        return CustomApiResponse.success(couponDataList);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 보유 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰 목록 조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                "couponId": 1,
                                "couponName": "10% 할인 쿠폰",
                                "totalQuantity": 1000,
                                "discountRate": 10,
                                "discountAmount": null,
                                "status": "active",
                                "startDate": "2025-04-10T00:00:00Z",
                                "endDate": "2025-12-31T23:59:59Z"
                            },{}...{}]
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 사용자입니다."
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
    public CustomApiResponse getUserCoupons(@PathVariable("userId") @Parameter(name = "userId", description = "사용자 id")Long userId){
        List<Map<String, Object>> couponDataList = new ArrayList<>();
        Map<String, Object> couponData = new LinkedHashMap<>();
        couponData.put("couponId", 1);
        couponData.put("couponName", "10% 할인 쿠폰");
        couponData.put("totalQuantity", 1000);
        couponData.put("discountRate", 10);
        couponData.put("discountAmount", "");
        couponData.put("status", "active");
        couponData.put("startDate", "2025-04-10T00:00:00Z");
        couponData.put("endDate", "2025-04-10T00:00:00Z");

        Map<String, Object> couponData2 = new LinkedHashMap<>();
        couponData2.put("couponId", 2);
        couponData2.put("couponName", "5000원 할인 쿠폰");
        couponData2.put("totalQuantity", 1000);
        couponData2.put("discountRate", "");
        couponData2.put("discountAmount", 5000);
        couponData2.put("status", "active");
        couponData2.put("startDate", "2025-04-10T00:00:00Z");
        couponData2.put("endDate", "2025-04-10T00:00:00Z");

        couponDataList.add(couponData);
        couponDataList.add(couponData2);

        return CustomApiResponse.success(couponDataList);
    }

    @PostMapping("/issue")
    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰 발급 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userCouponId": 1,
                                "userId": 1,
                                "couponId": 1,
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:30:00Z"
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류 - 정의되지 않은 사용자", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 사용자입니다."
                        }
                        """),
                    @ExampleObject(name = "검증 오류 - 쿠폰 ID", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 쿠폰 ID입니다."
                        }
                        """),
                    @ExampleObject(name = "이미 발급된 쿠폰인 경우", value = """
                        {
                            "code": "400",
                             "message": "이미 발급된 쿠폰입니다."
                        }
                        """),
                    @ExampleObject(name = "쿠폰이 모두 소진된 경우", value = """
                        {
                            "code": "400",
                             "message": "쿠폰이 모두 소진되었습니다."
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
    public CustomApiResponse getUserCoupons(
            @RequestBody
            @Schema(description = "쿠폰 발급 시 필요한 정보",
                    example = "{\"userId\": 123, \"couponId\": 1}")
            Map<String,Object> couponIssueRequest
    ){
        Map<String, Object> couponData = new LinkedHashMap<>();
        couponData.put("userCouponId", 1);
        couponData.put("userId", 1);
        couponData.put("couponId", 1);
        couponData.put("createDate", "2025-04-10T00:00:00Z");
        couponData.put("updateDate", "2025-04-10T00:00:00Z");

        return CustomApiResponse.success(couponData);
    }

}
