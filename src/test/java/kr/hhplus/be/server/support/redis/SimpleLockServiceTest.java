package kr.hhplus.be.server.support.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SimpleLockServiceTest {

    @Autowired
    private SimpleLockService simpleLockService;

    @Test
    public void testSimpleLock() throws InterruptedException{
        String key = "lock:user:123";

        boolean lockecd = simpleLockService.trySimpleLock(key, Duration.ofSeconds(10));
        assertThat(lockecd).isTrue();

        AtomicBoolean secondTryResult = new AtomicBoolean();
        Thread t = new Thread(() -> {
            boolean secondTry = simpleLockService.trySimpleLock(key, Duration.ofSeconds(5));
            secondTryResult.set(secondTry);
        });
        t.start();
        t.join();

        assertThat(secondTryResult.get()).isFalse();
    }
}
