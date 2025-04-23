package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private void tryChargeWithRetry(Long userId, Long amount, int maxRetry) {
        int retry = 0;
        while (retry < maxRetry) {
            try {
                userPointService.charge(userId, amount);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                retry++;
                System.out.println("충돌 발생 (충전), 재시도: " + retry);
                try {
                    Thread.sleep(10); // 너무 빨리 재시도하지 않도록 살짝 대기
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("충전 실패: 최대 재시도 초과");
    }

    private void tryUseWithRetry(Long userId, Long amount, int maxRetry) {
        int retry = 0;
        while (retry < maxRetry) {
            try {
                userPointService.use(userId, amount);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                retry++;
                System.out.println("충돌 발생 (사용), 재시도: " + retry);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException("사용 실패: 최대 재시도 초과");
    }

    @BeforeEach
    void setUp(){
        User user = new User("하늘", true);
        userRepository.save(user);
        userId = user.getUserId();
        UserPoint userPoint = new UserPoint(userId, 100L);
        userPointRepository.save(userPoint);
    }

    @Test
    void 동시에_충전_요청() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    tryChargeWithRetry(userId, 10L, 5);
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

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(200L), userPoint.getPoint());

    }

    @Test
    void 동시에_사용_요청() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    tryUseWithRetry(userId, 10L, 5);
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

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(0L), userPoint.getPoint());

    }
}
