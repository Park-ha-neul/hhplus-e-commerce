package kr.hhplus.be.server.infrastructure;

import kr.hhplus.be.server.domain.product.TopProduct;
import kr.hhplus.be.server.domain.product.TopProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class TopProductRepositoryPerformanceTest {

    @Autowired
    private TopProductRepository topProductRepository;

    private LocalDate testDate;

    @BeforeEach
    public void setUp() {
        testDate = LocalDate.of(2025, 4, 23);
    }

    @Test
    public void testFindByPeriodTypeAndCalculateDateOrderByRankAsc_Performance() {
        // 성능 테스트 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // 페이지 크기 설정 (예시: 100개씩 조회)
        PageRequest pageRequest = PageRequest.of(0, 100);

        // 실제 성능 테스트: 인기 상품 조회
        List<TopProduct> topProducts = topProductRepository.findByPeriodTypeAndCalculateDateOrderByRankAsc(
                TopProduct.PeriodType.DAILY, testDate, pageRequest);

        // 성능 테스트 종료 시간 기록
        long endTime = System.currentTimeMillis();

        // 성능 테스트 결과 출력 (밀리초 단위)
        System.out.println("조회 시간: " + (endTime - startTime) + "ms");

        assertNotNull(topProducts);
        System.out.println("조회된 상품 수: " + topProducts.size());
    }
}
