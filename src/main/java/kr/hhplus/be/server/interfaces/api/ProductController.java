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
@RequestMapping("/products")
@Tag(name = "📌 상품 관리", description = "상품 관련 API 모음")
public class ProductController {
    @GetMapping("/")
    @Operation(summary = "상품 목록 조회", description = "상품의 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                 "productId": 1,
                                 "productName": "A 상품",
                                 "description": "좋은 상품입니다,",
                                 "quantity": 3,
                                 "status": "판매중",
                                 "price": 5000,
                                 "createDate": "2024-04-02T12:00:00Z",
                                 "updateDate": "2024-04-02T12:30:00Z"
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
    public CustomApiResponse getProducts(){
        List<Map<String, Object>> productList = new ArrayList<>();

        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", 1);
        productData.put("productName", "A 상품");
        productData.put("description", "좋은 상품입니다.");
        productData.put("quantity", 3);
        productData.put("status", "판매중");
        productData.put("price", 5000);
        productData.put("createDate", "2024-04-02T12:00:00Z");
        productData.put("updateDate", "2024-04-02T12:00:00Z");

        Map<String, Object> productData1 = new LinkedHashMap<>();
        productData1.put("productId", 2);
        productData1.put("productName", "B 상품");
        productData1.put("description", "좋은 상품입니다.");
        productData1.put("quantity", 13);
        productData1.put("status", "판매중");
        productData1.put("price", 3000);
        productData1.put("createDate", "2024-04-02T12:00:00Z");
        productData1.put("updateDate", "2024-04-02T12:00:00Z");

        productList.add(productData);
        productList.add(productData1);

        return CustomApiResponse.success(productList);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상세조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                 "productId": 1,
                                 "productName": "A 상품",
                                 "description": "좋은 상품입니다,",
                                 "quantity": 3,
                                 "status": "판매중",
                                 "price": 5000,
                                 "createDate": "2024-04-02T12:00:00Z",
                                 "updateDate": "2024-04-02T12:30:00Z"
                               }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "존재하지 않는 상품입니다."
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
    public CustomApiResponse getProductsDetail(@PathVariable("productId") @Parameter(name = "productId", description = "상품 ID") Long productId){
        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", productId);
        productData.put("productName", "A 상품");
        productData.put("description", "좋은 상품입니다.");
        productData.put("quantity", 3);
        productData.put("status", "판매중");
        productData.put("price", 5000);
        productData.put("createDate", "2024-04-02T12:00:00Z");
        productData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(productData);
    }

    @PostMapping("/")
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품 등록 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                                 "productId": 1,
                                 "productName": "A 상품",
                                 "description": "좋은 상품입니다,",
                                 "quantity": 3,
                                 "status": "판매중",
                                 "price": 5000,
                                 "createDate": "2024-04-02T12:00:00Z",
                                 "updateDate": "2024-04-02T12:30:00Z"
                               }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품명이 없는 경우", value = """
                        {
                            "code": "400",
                             "message": "상품명이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "수량이 없는 경우", value = """
                        {
                            "code": "400",
                             "message": "수량이 누락되었습니다. 필수 값을 확인해주세요."
                        }
                        """),
                    @ExampleObject(name = "가격이 없는 경우", value = """
                        {
                            "code": "400",
                             "message": "가격이 누락되었습니다. 필수 값을 확인해주세요."
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
    public CustomApiResponse createProduct(
            @RequestBody
            @Schema(description = "상품 정보",
                    example = "{\"name\": \"A 상품\", \"decription\": \"\", \"quantity\": 3, \"price\": 5000}")
            Map<String,Object> productRequest
    ){
        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", 1);
        productData.put("productName", "A 상품");
        productData.put("description", "좋은 상품입니다.");
        productData.put("quantity", 3);
        productData.put("status", "판매중");
        productData.put("price", 5000);
        productData.put("createDate", "2024-04-02T12:00:00Z");
        productData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(productData);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "인기 상품을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "인기 상품 조회 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": [{
                                 "productId": 1,
                                 "productName": "A 상품",
                                 "description": "좋은 상품입니다,",
                                 "quantity": 3,
                                 "status": "판매중",
                                 "price": 5000,
                                 "createDate": "2024-04-02T12:00:00Z",
                                 "updateDate": "2024-04-02T12:30:00Z"
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
    public CustomApiResponse getPopularProducts(){
        List<Map<String, Object>> productList = new ArrayList<>();

        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", 1);
        productData.put("productName", "A 상품");
        productData.put("description", "좋은 상품입니다.");
        productData.put("quantity", 3);
        productData.put("status", "판매중");
        productData.put("price", 5000);
        productData.put("createDate", "2024-04-02T12:00:00Z");
        productData.put("updateDate", "2024-04-02T12:00:00Z");

        Map<String, Object> productData1 = new LinkedHashMap<>();
        productData1.put("productId", 2);
        productData1.put("productName", "B 상품");
        productData1.put("description", "좋은 상품입니다.");
        productData1.put("quantity", 13);
        productData1.put("status", "판매중");
        productData1.put("price", 3000);
        productData1.put("createDate", "2024-04-02T12:00:00Z");
        productData1.put("updateDate", "2024-04-02T12:00:00Z");

        productList.add(productData);
        productList.add(productData1);

        return CustomApiResponse.success(productList);
    }

    @PatchMapping("/")
    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품 수정 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                               "productId": 1,
                               "productName": "A 상품",
                               "description": "좋은 상품입니다,",
                               "quantity": 2, // 남은 재고 수정\s
                               "status": "판매중",
                               "price": 5000,
                               "createDate": "2024-04-02T12:00:00Z",
                               "updateDate": "2024-04-02T12:30:00Z"
                             }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 상품입니다."
                        }
                        """),
                    @ExampleObject(name = "상품 id 없이 수정 요청을 한 경우", value = """
                        {
                            "code": "400",
                             "message": "상품 id가 누락되었습니다. 필수 값을 확인해주세요."
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
    public CustomApiResponse updateProduct(
            @RequestBody
            @Schema(description = "수정할 상품 정보",
                    example = "{\"productId\": 1, \"productName\": \"새로운 상품명\", \"price\": 5000}")
            Map<String,Object> productRequest
    ){
        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", 1);
        productData.put("productName", "새로운 상품명");
        productData.put("description", "좋은 상품입니다.");
        productData.put("quantity", 3);
        productData.put("status", "판매중");
        productData.put("price", 5000);
        productData.put("createDate", "2024-04-02T12:00:00Z");
        productData.put("updateDate", "2024-04-02T12:00:00Z");

        return CustomApiResponse.success(productData);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품 삭제 성공", value = """
                        {
                            "code": 200,
                            "message": "SUCCESS",
                            "data": {
                               "productId": 1
                             }
                        }
                        """),
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "검증 오류", value = """
                        {
                            "code": "400",
                             "message": "정의되지 않은 상품입니다."
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
    public CustomApiResponse deleteProduct(@PathVariable ("productId") @Parameter(name = "productId", description = "삭제할 상품 id") Long productId){
        Map<String, Object> productData = new LinkedHashMap<>();
        productData.put("productId", productId);

        return CustomApiResponse.success(productData);
    }
}
