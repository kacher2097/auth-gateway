package com.authenhub.config.kafka;

import com.authenhub.dto.kafka.InventoryEvent;
import com.authenhub.dto.kafka.ReplenishmentEvent;
import com.authenhub.dto.kafka.SalesEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConfig kafkaConfig;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        // Cấu hình JsonDeserializer cho message value
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.setRemoveTypeHeaders(false); // Để tự mapping
        jsonDeserializer.setUseTypeMapperForKey(false); // Key không cần mapping
        jsonDeserializer.addTrustedPackages("com.authenhub.dto.kafka"); // Hạn chế trusted packages

        // Sử dụng DefaultTypeMapper thay vì TYPE_MAPPINGS string
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("inventoryEvent", InventoryEvent.class);
        idClassMapping.put("salesEvent", SalesEvent.class);
        idClassMapping.put("replenishmentEvent", ReplenishmentEvent.class);
        typeMapper.setIdClassMapping(idClassMapping);
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        jsonDeserializer.setTypeMapper(typeMapper);

        // Properties cho Kafka consumer
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);

        // Hiệu năng thêm (tuỳ chọn)
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

        // Bọc ErrorHandlingDeserializer để tránh crash khi deserialize lỗi
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(configProps);
//        return new DefaultKafkaConsumerFactory<>(
//                configProps,
//                new ErrorHandlingDeserializer<>(new StringDeserializer()),
//                new ErrorHandlingDeserializer<>(jsonDeserializer)
//        );
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
//        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2); // Số lượng luồng xử lý tin nhắn
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000); // 5 seconds poll timeout
        factory.setAutoStartup(false); // Don't start automatically if Kafka is not available

        // Sử dụng executor riêng cho Kafka
//        factory.getContainerProperties().setTaskExecutor(kafkaTaskExecutor());

        // Đặt cấu hình cho consumer threads
        factory.setBatchListener(true);

        // Cấu hình Kafka để chạy trên thread riêng
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setSyncCommits(true);

        // Cấu hình xử lý lỗi
        org.springframework.kafka.listener.DefaultErrorHandler errorHandler =
                new org.springframework.kafka.listener.DefaultErrorHandler(
                        new org.springframework.util.backoff.FixedBackOff(1000L, 3)
                );

        // Cho phép xử lý các lỗi deserialization
        errorHandler.addNotRetryableExceptions(
                org.springframework.kafka.support.serializer.DeserializationException.class,
                org.apache.kafka.common.errors.RecordDeserializationException.class,
                org.springframework.kafka.support.converter.ConversionException.class
        );

        factory.setCommonErrorHandler(errorHandler);

        return factory;
//        return factory;
    }
}
