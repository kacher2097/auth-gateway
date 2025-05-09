package com.authenhub.config.kafka;

import com.authenhub.dto.kafka.InventoryEvent;
import com.authenhub.dto.kafka.ReplenishmentEvent;
import com.authenhub.dto.kafka.SalesEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfig kafkaConfig;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        // Cấu hình JsonSerializer cho message value
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false); // Không gửi __TypeId__ header

        // Sử dụng DefaultTypeMapper thay vì TYPE_MAPPINGS string
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("inventoryEvent", InventoryEvent.class);
        idClassMapping.put("salesEvent", SalesEvent.class);
        idClassMapping.put("replenishmentEvent", ReplenishmentEvent.class);
        typeMapper.setIdClassMapping(idClassMapping);
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        jsonSerializer.setTypeMapper(typeMapper);

        // Cấu hình properties cho Kafka producer
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, jsonSerializer);

        // Reliability & throughput
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);         // Gộp nhiều message nhỏ
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);     // 16KB batch size

        return new DefaultKafkaProducerFactory<>(configProps);
//        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), jsonSerializer);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
