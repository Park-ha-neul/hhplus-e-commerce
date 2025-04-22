package kr.hhplus.be.server.concurrency;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class PointConcurrencyTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

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
    void 동시에_충전_요청() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.charge(userId, 100L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(200L), userPoint.getPoint());

    }

    @Test
    void 동시에_사용_요청() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.use(userId, 100L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        UserPoint userPoint = userPointRepository.findByUserId(userId);
        assertEquals(Long.valueOf(0L), userPoint.getPoint());

    }
}
