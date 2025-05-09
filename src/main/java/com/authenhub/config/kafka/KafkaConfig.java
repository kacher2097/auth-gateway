package com.authenhub.config.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Getter
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:inventory-group}")
    private String groupId;

    // Kafka Topics
    public static final String TOPIC_INVENTORY_CHANGES = "inventory-changes";
    public static final String TOPIC_SALES_EVENTS = "sales-events";
    public static final String TOPIC_REPLENISHMENT_EVENTS = "replenishment-events";

    @Bean(name = "kafkaTaskExecutor")
    public Executor kafkaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // Số luồng cơ bản
        executor.setMaxPoolSize(8); // Số luồng tối đa
        executor.setQueueCapacity(500); // Kích thước hàng đợi
        executor.setThreadNamePrefix("kafka-"); // Tiền tố tên luồng
        executor.initialize();
        return executor;
    }

    // Admin configuration for creating topics
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    // Create topics
    @Bean
    public NewTopic inventoryChangesTopic() {
        return new NewTopic(TOPIC_INVENTORY_CHANGES, 3, (short) 1);
    }

    @Bean
    public NewTopic salesEventsTopic() {
        return new NewTopic(TOPIC_SALES_EVENTS, 3, (short) 1);
    }

    @Bean
    public NewTopic replenishmentEventsTopic() {
        return new NewTopic(TOPIC_REPLENISHMENT_EVENTS, 3, (short) 1);
    }
}
