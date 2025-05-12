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
    private final PopularProductService popularProductService;

    @GetMapping("/")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public CustomApiResponse getProducts(
            @RequestParam(value = "status", required = false) Product.ProductStatus status
    ){
        List<Product> data = productService.getProducts(status);
        List<ProductResponse> response = ProductResponse.fromProducts(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @GetMapping("/{product_id}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세 내용을 조회합니다.")
    public CustomApiResponse getProduct(@PathVariable("product_id") @Parameter(name = "productId", description = "상품 ID") Long productId){
        Product data = productService.getProduct(productId);
        ProductResponse response = ProductResponse.fromProdcut(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }

    @PostMapping("/")
    @Operation(summary = "상품 등록", description = "상품을 등록합니다.")
    public CustomApiResponse createProduct(
            @RequestBody ProductRequest request,
            @RequestParam(value = "userId", required = false) Long userId
    ){
        try{
            ProductCommand command = request.toCommand();
            ProductResult productResult = productService.createProduct(command, userId);
            ProductResponse response = ProductResponse.from(productResult);
            return CustomApiResponse.success(ApiMessage.CREATE_SUCCESS, response);
        }catch (IllegalArgumentException e ){
            return CustomApiResponse.notFound(ApiMessage.INVALID_USER);
        }catch (ForbiddenException e){
            return CustomApiResponse.forbidden(ApiMessage.FORBIDDEN_ACCESS);
        }catch (Exception e){
            return CustomApiResponse.internalError(ApiMessage.SERVER_ERROR);
        }
    };

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 목록 조회", description = "topN 상품을 기간별로 조회합니다.")
    public CustomApiResponse getPopularProducts(
            @RequestParam(value = "periodType")PopularProduct.PeriodType periodType,
            @RequestParam(value = "ascending") boolean ascending,
            @RequestParam(value = "limit") int limit
    ) {
        List<PopularProduct> data = popularProductService.getPopularProducts(periodType, ascending, limit);
        List<PopularProductResponse> response = PopularProductResponse.fromPopularProducts(data);
        return CustomApiResponse.success(ApiMessage.VIEW_SUCCESS, response);
    }
}
