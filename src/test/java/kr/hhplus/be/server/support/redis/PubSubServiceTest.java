package kr.hhplus.be.server.support.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PubSubServiceTest {

    @Autowired
    private PubSubService pubSubService;

    @Test
    public void testPubSub() throws InterruptedException{
        String topic = "test:pubsub";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receiveMessage = new AtomicReference<>();

        pubSubService.subscribe(topic, (channel, msg) -> {
            System.out.println("Received: " + msg);
            receiveMessage.set(msg);
            latch.countDown();
        });

        String testMessage = "Hello from Redisson!";
        pubSubService.publish(topic, testMessage);

        boolean success = latch.await(2, TimeUnit.SECONDS);
        assertThat(success).isTrue();
        assertThat(receiveMessage.get()).isEqualTo(testMessage);
    }
}
