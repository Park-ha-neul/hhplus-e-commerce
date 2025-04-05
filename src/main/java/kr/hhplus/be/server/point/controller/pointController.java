package kr.hhplus.be.server.point.controller;

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

import java.util.*;

@RestController
@RequestMapping("/points")
@Tag(name = "📌 사용자 포인트 관리", description = "포인트 관련 API 모음")
public class pointController {

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 포인트 조회", description = "사용자의 보유 포인트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userId": 1,
                                "point": 5000,
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:30:00Z"
                            }
                        }
                        """),
                    @ExampleObject(name = "신규 사용자 포인트를 조회한 경우", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userId": 1,
                                "point": 0,
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:30:00Z"
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패", value = """
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
    public CustomApiResponse getUserPoints(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        Map<String, Object> pointData = new LinkedHashMap<>();
        pointData.put("userId", userId);
        pointData.put("point", 5000);
        pointData.put("createDate", "2024-04-02T12:00:00Z");
        pointData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(pointData);
    }

    @PostMapping("/{userId}")
    @Operation(summary = "사용자 포인트 등록", description = "사용자가 없는 경우 사용자 포인트에 데이터를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userId": 1,
                                "point": 5000,
                                "createDate": "2024-04-02T12:00:00Z",
                                "updateDate": "2024-04-02T12:30:00Z"
                            }
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
    public CustomApiResponse registPoint(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        Map<String, Object> pointData = new LinkedHashMap<>();
        pointData.put("userId", userId);
        pointData.put("point", 0);
        pointData.put("createDate", "2024-04-02T12:00:00Z");
        pointData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(pointData);
    }

    @PostMapping("/charge")
    @Operation(summary = "사용자 포인트 충전", description = "사용자의 포인트를 충전합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "충전 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userId": 123,
                                "chargedAmount": 5000,
                                "newBalance": 6000
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패", value = """
                        {
                            "code": "400",
                            "message": "정의되지 않은 사용자입니다."
                        }
                        """),
                    @ExampleObject(name = "충전 실패: 1회 최대 충전량 초과", value = """
                        {
                            "code": "400",
                            "message": "1회 최대 충전량(100,000)을 초과하였습니다."
                        }
                        """),
                    @ExampleObject(name = "충전 실패: 1인 최대 한도 초과", value = """
                        {
                            "code": "400",
                            "message": "1인 사용자의 최대 한도(1,000,000)를 초과하였습니다."
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
    public CustomApiResponse chargePoint(
            @RequestBody
            @Schema(description = "충전 정보",
                    example = "{\"userId\": 123, \"point\": 5000}")
            Map<String,Object> chargeRequest
    ){
        Map<String, Object> chargeData = new LinkedHashMap<>();
        chargeData.put("userId", 123);
        chargeData.put("chargedAmount", 5000);
        chargeData.put("newBalance", 6000);
        chargeData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(chargeData);
    }

    @PostMapping("/use")
    @Operation(summary = "사용자 포인트 사용", description = "사용자의 포인트를 사용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "포인트 사용 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                "userId": 123,
                                "usedAmount": 2000,
                                "newBalance": 6000
                            }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패", value = """
                        {
                            "code": "400",
                            "message": "정의되지 않은 사용자입니다."
                        }
                        """),
                    @ExampleObject(name = "포인트 사용 실패", value = """
                        {
                            "code": "400",
                            "message": "포인트 잔액이 부족합니다."
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
    public CustomApiResponse usePoint(
            @RequestBody
            @Schema(description = "포인트 사용 정보",
                    example = "{\"userId\": 123, \"amount\": 2000}")
            Map<String,Object> useRequest
    ){
        Map<String, Object> useData = new LinkedHashMap<>();
        useData.put("userId", 123);
        useData.put("usedAmount", 2000);
        useData.put("newBalance", 6000);

        return CustomApiResponse.success(useData);
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "사용자 포인트 이력 조회", description = "특정 사용자의 포인트 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "이력 조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                             "historyId": 1,
                             "amount": 200, // 변동된 포인트
                             "balanceBefore": 2000, // 변경 전 포인트 잔액\s
                             "balanceAfter": 1800, // 변경 후 포인트 잔액\s
                             "transcation": "use",
                             "createDate": "2024-04-02T12:00:00Z"
                           }, {
                             "historyId": 1,
                             "amount": 200, // 변동된 포인트
                             "balanceBefore": 2000, // 변경 전 포인트 잔액\s
                             "balanceAfter": 1800, // 변경 후 포인트 잔액\s
                             "transcation": "charge",
                             "createDate": "2024-04-02T12:00:00Z"
                           }]
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 실패", value = """
                        {
                            "code": "400",
                            "message": "정의되지 않은 사용자입니다."
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
    public CustomApiResponse getUserPointHistory(@PathVariable("userId") @Parameter(name = "userId", description = "사용자의 ID") Long userId){
        List<Map<String, Object>> historyDataList = new ArrayList<>();  // List<Map<String, Object>>로 초기화

        // 첫 번째 이력
        Map<String, Object> history1 = new HashMap<>();
        history1.put("historyId", 1);
        history1.put("amount", 200);
        history1.put("balanceBefore", 2000);
        history1.put("balanceAfter", 1800);
        history1.put("transaction", "use");
        history1.put("createDate", "2024-04-02T12:00:00Z");

        // 두 번째 이력
        Map<String, Object> history2 = new HashMap<>();
        history2.put("historyId", 2);
        history2.put("amount", 200);
        history2.put("balanceBefore", 2000);
        history2.put("balanceAfter", 1800);
        history2.put("transaction", "charge");
        history2.put("createDate", "2024-04-02T12:00:00Z");

        // historyDataList에 이력 추가
        historyDataList.add(history1);
        historyDataList.add(history2);

        return CustomApiResponse.success(historyDataList);
    }
}
