package kr.hhplus.be.server.interfaces.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class KafkaAdminClientConfig {

    @Bean
    public AdminClient adminClient(KafkaProperties kafkaProperties) {
        Map<String, Object> config = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                String.join(",", kafkaProperties.getBootstrapServers())
        );
        return AdminClient.create(config);
    }
}
