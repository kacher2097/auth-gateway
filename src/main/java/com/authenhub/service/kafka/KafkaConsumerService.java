package com.authenhub.service.kafka;

import com.authenhub.config.kafka.KafkaConfig;
import com.authenhub.dto.kafka.InventoryEvent;
import com.authenhub.dto.kafka.ReplenishmentEvent;
import com.authenhub.dto.kafka.SalesEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final AtomicInteger inventoryRetryCount = new AtomicInteger(0);
    private final AtomicInteger salesRetryCount = new AtomicInteger(0);
    private final AtomicInteger replenishmentRetryCount = new AtomicInteger(0);

    @KafkaListener(
            topics = KafkaConfig.TOPIC_INVENTORY_CHANGES,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:inventory-group}",
            autoStartup = "false",
            id = "inventoryConsumer"
    )
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void consumeInventoryEvent(
            @Payload InventoryEvent event,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received inventory event: {} from topic: {}, partition: {}, offset: {}",
                    event, topic, partition, offset);

            // Process the event here
            // This could include updating caches, sending notifications, etc.

            // Reset retry count on success
            inventoryRetryCount.set(0);

            // Acknowledge the message after successful processing
            ack.acknowledge();
            log.info("Successfully processed inventory event: {}", event.getEventId());
        } catch (Exception e) {
            int retries = inventoryRetryCount.incrementAndGet();
            log.error("Error processing inventory event: {} (Attempt: {})", event, retries, e);

            if (retries >= 3) {
                log.error("Max retries reached for inventory event: {}. Acknowledging message to prevent blocking.", event.getEventId());
                inventoryRetryCount.set(0);
                ack.acknowledge();
                // In a production environment, you might want to send this to a dead letter queue
            } else {
                // Re-throw to trigger retry
                throw e;
            }
        }
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_SALES_EVENTS,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:inventory-group}",
            autoStartup = "false",
            id = "salesConsumer"
    )
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void consumeSalesEvent(
            @Payload SalesEvent event,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received sales event: {} from topic: {}, partition: {}, offset: {}",
                    event, topic, partition, offset);

            // Process the event here
            // This could include updating analytics, sending notifications, etc.

            // Reset retry count on success
            salesRetryCount.set(0);

            // Acknowledge the message after successful processing
            ack.acknowledge();
            log.info("Successfully processed sales event: {}", event.getEventId());
        } catch (Exception e) {
            int retries = salesRetryCount.incrementAndGet();
            log.error("Error processing sales event: {} (Attempt: {})", event, retries, e);

            if (retries >= 3) {
                log.error("Max retries reached for sales event: {}. Acknowledging message to prevent blocking.", event.getEventId());
                salesRetryCount.set(0);
                ack.acknowledge();
                // In a production environment, you might want to send this to a dead letter queue
            } else {
                // Re-throw to trigger retry
                throw e;
            }
        }
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_REPLENISHMENT_EVENTS,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id:inventory-group}",
            autoStartup = "false",
            id = "replenishmentConsumer"
    )
    @Retryable(value = Exception.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void consumeReplenishmentEvent(
            @Payload ReplenishmentEvent event,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received replenishment event: {} from topic: {}, partition: {}, offset: {}",
                    event, topic, partition, offset);

            // Process the event here
            // This could include updating dashboards, sending notifications, etc.

            // Reset retry count on success
            replenishmentRetryCount.set(0);

            // Acknowledge the message after successful processing
            ack.acknowledge();
            log.info("Successfully processed replenishment event: {}", event.getEventId());
        } catch (Exception e) {
            int retries = replenishmentRetryCount.incrementAndGet();
            log.error("Error processing replenishment event: {} (Attempt: {})", event, retries, e);

            if (retries >= 3) {
                log.error("Max retries reached for replenishment event: {}. Acknowledging message to prevent blocking.", event.getEventId());
                replenishmentRetryCount.set(0);
                ack.acknowledge();
                // In a production environment, you might want to send this to a dead letter queue
            } else {
                // Re-throw to trigger retry
                throw e;
            }
        }
    }
}
