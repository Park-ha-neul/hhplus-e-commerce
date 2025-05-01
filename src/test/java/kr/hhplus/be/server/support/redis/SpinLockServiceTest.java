package kr.hhplus.be.server.support.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SpinLockServiceTest {

    @Autowired
    private SpinLockService spinLockService;

    @Test
    public void testSpinLock() throws InterruptedException {
        String key = "lock:user:123";
        String value1 = "tx1";
        String value2 = "tx2";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean thread2Result = new AtomicBoolean(false);

        // Thread 1: 락 먼저 선점 후 2초 유지
        Thread t1 = new Thread(() -> {
            boolean locked = spinLockService.trySpinLock(key, value1, Duration.ofSeconds(5), Duration.ofSeconds(2), Duration.ofMillis(100));
            assertThat(locked).isTrue();

            try {
                Thread.sleep(2000); // 락 유지
            } catch (InterruptedException ignored) {
            }

            spinLockService.releaseLock(key);
            latch.countDown();
        });

        // Thread 2: 락이 해제될 때까지 재시도
        Thread t2 = new Thread(() -> {
            boolean locked = spinLockService.trySpinLock(key, value2, Duration.ofSeconds(5), Duration.ofSeconds(5), Duration.ofMillis(100));
            thread2Result.set(locked);
        });

        t1.start();
        Thread.sleep(100); // 락 선점 우선권 확보
        t2.start();

        latch.await();
        t1.join();
        t2.join();

        assertThat(thread2Result.get()).isTrue(); // spin 후 성공했어야 함
        spinLockService.releaseLock(key);
    }
}
