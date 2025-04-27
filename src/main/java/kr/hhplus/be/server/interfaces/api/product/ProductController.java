package kr.hhplus.be.server.interfaces.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final ProductService productService;
    private final TopProductService topProductService;

    @GetMapping("/")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public CustomApiResponse getProducts(
            @RequestParam(value = "status", required = false) Product.ProductStatus status
    ){
        List<Product> data = productService.getProducts(status);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @GetMapping("/{product_id}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세 내용을 조회합니다.")
    public CustomApiResponse getProduct(@PathVariable("product_id") @Parameter(name = "productId", description = "상품 ID") Long productId){
        Product data = productService.getProduct(productId);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }

    @PostMapping("/")
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    public CustomApiResponse createProduct(
            @RequestBody ProductRequest request,
            @RequestParam(value = "userId", required = false) Long userId
    ){
        try{
            Product result = productService.createProduct(request, userId);
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
            @RequestParam(value = "periodType") TopProduct.PeriodType periodType
    ) {
        List<TopProduct> data = topProductService.getTopProductsByPeriod(periodType);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, data);
    }
}
