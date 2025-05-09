package com.authenhub.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Configuration
@EnableScheduling
public class KafkaListenerConfig {

    @Autowired
    private KafkaAdmin kafkaAdmin;
    
    @Autowired(required = false)
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    
    private boolean kafkaAvailable = false;

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        if (kafkaListenerEndpointRegistry != null) {
            checkKafkaAvailability();
        } else {
            log.warn("KafkaListenerEndpointRegistry is not available. Kafka listeners will not be managed.");
        }
    }

    @Scheduled(fixedDelay = 60000) // Check every minute
    public void checkKafkaAvailability() {
        if (kafkaListenerEndpointRegistry == null) {
            return; // Skip if registry is not available
        }
        
        try {
            // Try to list topics to check if Kafka is available
            adminClient().listTopics().names().get(5, TimeUnit.SECONDS);
            
            if (!kafkaAvailable) {
                log.info("Kafka is now available. Starting Kafka listeners.");
                kafkaAvailable = true;
                startKafkaListeners();
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (kafkaAvailable) {
                log.warn("Kafka is no longer available. Stopping Kafka listeners.", e);
                kafkaAvailable = false;
                stopKafkaListeners();
            } else {
                log.warn("Kafka is not available. Kafka listeners will not be started.", e);
            }
        }
    }

    private void startKafkaListeners() {
        if (kafkaListenerEndpointRegistry != null) {
            kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
                if (!container.isRunning()) {
                    container.start();
                    log.info("Started Kafka listener container: {}", container.getListenerId());
                }
            });
        }
    }

    private void stopKafkaListeners() {
        if (kafkaListenerEndpointRegistry != null) {
            kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
                if (container.isRunning()) {
                    container.stop();
                    log.info("Stopped Kafka listener container: {}", container.getListenerId());
                }
            });
        }
    }
}
