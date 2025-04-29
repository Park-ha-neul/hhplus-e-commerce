package kr.hhplus.be.server.interfaces.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.support.ResponseCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TopProductRepository topProductRepository;

    @Test
    void 상품_목록_조회_성공() throws Exception {
        // given
        Product product1 = new Product(1L, "상품 1", "설명 1", 1000L, 200L, Product.ProductStatus.AVAILABLE);
        productRepository.save(product1);
        Product product2 = new Product(2L, "상품 2", "설명 2", 1000L, 200L, Product.ProductStatus.AVAILABLE);
        productRepository.save(product2);

        // when & then
        mockMvc.perform(get("/products/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].name").value(product1.getName()))
                .andExpect(jsonPath("$.data[1].name").value(product2.getName()));
    }

    @Test
    void 상품_상세_조회_성공() throws Exception {
        // given
        Product saved = productRepository.save(new Product(1L, "상품 1", "설명 1", 1000L, 200L, Product.ProductStatus.AVAILABLE));

        // when & then
        mockMvc.perform(get("/products/{product_id}", saved.getProductId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.name").value(saved.getName()));
    }

    @Test
    void 상품_등록_성공() throws Exception {
        // given
        Long userId = 1L;
        ProductCommand command = new ProductCommand("상품 A", "설명", 3000L, 2000L);

        // when & then
        mockMvc.perform(post("/products/")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data.name").value(command.getName()));
    }

    @Test
    void 인기_상품_조회_성공() throws Exception {
        // given
        Product product = productRepository.save(new Product(1L, "상품 1", "설명 1", 1000L, 200L, Product.ProductStatus.AVAILABLE));
        TopProduct topProduct = new TopProduct(product.getProductId(), 1, 100L, LocalDate.now(), TopProduct.PeriodType.WEEKLY);
        topProductRepository.save(topProduct);

        // when & then
        mockMvc.perform(get("/products/popular")
                        .param("periodType", "WEEKLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.data[0].rank").value(1));
    }
}
