package kr.hhplus.be.server.interfaces.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductService;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private TopProductService topProductService;

    @Test
    void 상품_목록_조회_성공() throws Exception {
        // given
        List<Product> mockProducts = List.of(
                new Product(1L, "상품1", "설명1", 10L, 1000L, Product.ProductStatus.AVAILABLE),
                new Product(2L, "상품2", "설명2", 5L, 2000L, Product.ProductStatus.AVAILABLE)
        );
        given(productService.getProducts(null)).willReturn(mockProducts);

        // when & then
        mockMvc.perform(get("/products/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].name").value("상품1"))
                .andExpect(jsonPath("$.data[1].name").value("상품2"));
    }

    @Test
    void 상품_상세_조회_성공() throws Exception {
        // given
        Long productId = 1L;
        Product mockProduct = new Product(productId, "상품1", "상세설명", 10L, 1000L, Product.ProductStatus.AVAILABLE);
        given(productService.getProductDetails(productId)).willReturn(mockProduct);

        // when & then
        mockMvc.perform(get("/products/{product_id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.name").value("상품1"));
    }

    @Test
    void 상품_등록_성공() throws Exception {
        // given
        Long userId = 1L;
        ProductRequest request = new ProductRequest("상품3", "설명3", 1000L, 10L);
        Product mockProduct = new Product(3L, "상품3", "설명3", 10L, 1000L, Product.ProductStatus.AVAILABLE);

        given(productService.createProduct(any(ProductRequest.class), eq(userId))).willReturn(mockProduct);

        // when & then
        mockMvc.perform(post("/products/")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.name").value("상품3"));
    }

    @Test
    void 인기_상품_조회_성공() throws Exception {
        // given
        TopProduct.PeriodType periodType = TopProduct.PeriodType.WEEKLY;
        List<TopProduct> topProducts = List.of(
                new TopProduct(1L, 1, 100L, LocalDate.now(), periodType)
        );

        given(topProductService.getTopProductsByPeriod(periodType)).willReturn(topProducts);

        // when & then
        mockMvc.perform(get("/products/popular")
                        .param("periodType", periodType.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].rank").value(1));
    }
}
