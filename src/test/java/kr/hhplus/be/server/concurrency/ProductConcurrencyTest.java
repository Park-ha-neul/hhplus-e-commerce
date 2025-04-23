package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.api.product.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        ProductRequest request = new ProductRequest("상품", "설명", 2000L, 10L);
        Product product = Product.create(request);
        productRepository.save(product);
        productId = product.getProductId();
    }

    @Test
    void 동시에_재고_차감_요청() throws Exception {
        int threadCount = 10;
        Long amount = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseProductBalance(productId, amount);
                } catch(Exception e){
                    System.out.println("예외 발생 : " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("수행 시간: " + (end - start) + "ms");

        Product product = productRepository.findById(productId).orElseThrow();
        assertEquals(Long.valueOf(0L), product.getQuantity());
    }
}
