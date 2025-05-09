package com.authenhub.service.kafka;

import com.authenhub.config.kafka.KafkaConfig;
import com.authenhub.dto.kafka.InventoryEvent;
import com.authenhub.dto.kafka.ReplenishmentEvent;
import com.authenhub.dto.kafka.SalesEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import java.util.UUID;

@Slf4j
@Service
public class KafkaProducerService {

    private final Executor kafkaTaskExecutor;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate,
                               @Qualifier("kafkaTaskExecutor") Executor kafkaTaskExecutor) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTaskExecutor = kafkaTaskExecutor;
    }

    public void sendInventoryEvent(InventoryEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        log.info("Sending inventory event: {}", event);

        // Tạo message với type information
        org.springframework.messaging.Message<InventoryEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaConfig.TOPIC_INVENTORY_CHANGES)
                .setHeader(KafkaHeaders.KEY, event.getSku())
                .setHeader("_type", "inventoryEvent")
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        future.thenAcceptAsync(result -> {
            log.info("Sent inventory event for SKU: {} with offset: {}",
                    event.getSku(), result.getRecordMetadata().offset());
        }, kafkaTaskExecutor).exceptionally(ex -> {
            log.error("Unable to send inventory event for SKU: {}", event.getSku(), ex);
            return null;
        });
        // Sử dụng executor riêng cho Kafka
    }

    public void sendSalesEvent(SalesEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        log.info("Sending sales event: {}", event);

        // Tạo message với type information
        org.springframework.messaging.Message<SalesEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaConfig.TOPIC_SALES_EVENTS)
                .setHeader(KafkaHeaders.KEY, event.getSku())
                .setHeader("_type", "salesEvent")
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        // Sử dụng executor riêng cho Kafka
        future.thenAcceptAsync(result -> {
            log.info("Sent sales event for SKU: {} with offset: {}",
                    event.getSku(), result.getRecordMetadata().offset());
        }, kafkaTaskExecutor).exceptionally(ex -> {
            log.error("Unable to send sales event for SKU: {}", event.getSku(), ex);
            return null;
        });
    }

    public void sendReplenishmentEvent(ReplenishmentEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }

        log.info("Sending replenishment event: {}", event);

        // Tạo message với type information
        org.springframework.messaging.Message<ReplenishmentEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, KafkaConfig.TOPIC_REPLENISHMENT_EVENTS)
                .setHeader(KafkaHeaders.KEY, event.getSku())
                .setHeader("_type", "replenishmentEvent")
                .build();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(message);

        // Sử dụng executor riêng cho Kafka
        future.thenAcceptAsync(result -> {
            log.info("Sent replenishment event for SKU: {} with offset: {}",
                    event.getSku(), result.getRecordMetadata().offset());
        }, kafkaTaskExecutor).exceptionally(ex -> {
            log.error("Unable to send replenishment event for SKU: {}", event.getSku(), ex);
            return null;
        });
    }
}
