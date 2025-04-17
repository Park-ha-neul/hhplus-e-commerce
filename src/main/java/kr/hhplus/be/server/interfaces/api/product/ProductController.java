package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.common.PeriodType;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.support.ApiMessage;
import kr.hhplus.be.server.support.CustomApiResponse;
import kr.hhplus.be.server.support.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "📌 상품 관리", description = "상품 관련 API 모음")
public class ProductController {
    private ProductService productService;
    private TopProductService topProductService;

    @GetMapping("/")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public CustomApiResponse getProducts(
            @RequestParam(required = false) ProductStatus status
    ){
        List<ProductEntity> data = productService.getProducts(status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @GetMapping("/{product_id}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세 내용을 조회합니다.")
    public CustomApiResponse getProduct(@PathVariable("productId") @Parameter(name = "productId", description = "상품 ID") Long productId){
        ProductEntity data = productService.getProductDetails(productId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @PostMapping("/")
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    public CustomApiResponse createProduct(
            @RequestBody ProductRequest request,
            @RequestParam Long userId
    ){
        try{
            ProductEntity result = productService.createProduct(request, userId);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, result);
        }catch (IllegalArgumentException e ){
            return CustomApiResponse.notFound(ApiMessage.INVALID_USER);
        }catch (ForbiddenException e){
            return CustomApiResponse.forbidden(ApiMessage.FORBIDDEN_ACCESS);
        }catch (Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    };

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "top5 상품을 기간별로 조회합니다.")
    public CustomApiResponse getPopularProducts(
            @RequestParam PeriodType periodType
    ) {
        List<TopProductEntity> data = topProductService.getTopProductsByPeriod(periodType);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }
}
