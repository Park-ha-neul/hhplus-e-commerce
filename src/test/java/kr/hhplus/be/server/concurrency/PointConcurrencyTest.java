package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp(){
        User user = new User("하늘", true);
        userRepository.save(user);
        userId = user.getUserId();
        UserPoint userPoint = new UserPoint(userId, 100L);
        userPointRepository.save(userPoint);
    }


    @Test
    void 동시에_충전_요청_낙관적락_검증() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.charge(userId, 10L);
                    successCount.incrementAndGet();
                } catch(Exception e){
                    System.out.println("예외 발생 : " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("수행 시간: " + (end - start) + "ms");

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(100L + (10L * successCount.get())), userPoint.getPoint());

    }

    @Test
    void 동시에_사용_요청_낙관적락_검증() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.use(userId, 10L);
                    successCount.incrementAndGet();
                } catch(Exception e){
                    System.out.println("예외 발생 : " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("수행 시간: " + (end - start) + "ms");

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(100L - (10L * successCount.get())), userPoint.getPoint());

    }
}
