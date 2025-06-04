package kr.hhplus.be.server.infrastructure.kafka.coupon;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class CouponIssuedProducer {
    private final KafkaTemplate<String, CouponIssuedMessage> kafkaTemplate;
    private final AdminClient adminClient;

    public CouponIssuedProducer(KafkaTemplate<String, CouponIssuedMessage> kafkaTemplate, AdminClient adminClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
    }

    public void send(CouponIssuedMessage message){
        String topic = "inside.coupon";

        LocalDateTime now = LocalDateTime.now();
        Long retentionMs = Duration.between(now, message.getEndDate()).toMillis();

        createTopicIfNotExists(topic, retentionMs);
        kafkaTemplate.send(topic, message.getCouponId().toString(), message);
    }

    private void createTopicIfNotExists(String topicName, Long retentionMs) {
        try {
            boolean exists = adminClient.listTopics().names().get().contains(topicName);
            if (!exists) {
                NewTopic topic = new NewTopic(topicName, 3, (short) 1) // 3개 파티션, 리플리카 1
                        .configs(Map.of(
                                TopicConfig.RETENTION_MS_CONFIG, String.valueOf(retentionMs) // 쿠폰 유효기간까지
                        ));
                adminClient.createTopics(Collections.singletonList(topic)).all().get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Kafka 토픽 생성 실패: " + topicName, e);
        }
    }
}
