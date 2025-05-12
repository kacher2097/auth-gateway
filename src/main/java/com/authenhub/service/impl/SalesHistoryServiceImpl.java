package com.authenhub.service.impl;

import com.authenhub.dto.inventory.CreateSalesHistoryRequest;
import com.authenhub.dto.inventory.SalesHistoryDTO;
import com.authenhub.dto.kafka.SalesEvent;
import com.authenhub.entity.Inventory;
import com.authenhub.entity.SalesHistory;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.kafka.service.KafkaProducerService;
import com.authenhub.repository.InventoryRepository;
import com.authenhub.repository.SalesHistoryRepository;
import com.authenhub.service.InventoryService;
import com.authenhub.service.SalesHistoryService;
import com.authenhub.utils.TimestampUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesHistoryServiceImpl implements SalesHistoryService {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final SalesHistoryRepository salesHistoryRepository;
//    private final KafkaProducerService kafkaProducerService;

    @Override
    public List<SalesHistoryDTO> getAllSalesHistory() {
        log.info("Fetching all sales history");
        return salesHistoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SalesHistoryDTO getSalesHistoryById(Long id) {
        log.info("Fetching sales history with id: {}", id);
        return salesHistoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Sales history not found with id: " + id));
    }

    @Override
    public List<SalesHistoryDTO> getSalesHistoryBySku(String sku) {
        log.info("Fetching sales history for SKU: {}", sku);
        return salesHistoryRepository.findBySku(sku).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalesHistoryDTO> getSalesHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching sales history between {} and {}", startDate, endDate);
        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);
        return salesHistoryRepository.findSalesBetweenDates(start, end).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesHistoryDTO createSalesHistory(CreateSalesHistoryRequest request, String userId) {
        log.info("Creating new sales history for SKU: {}", request.getSku());

        // Verify that the SKU exists
        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + request.getSku()));

        // Update inventory quantity
        inventoryService.updateInventoryQuantity(request.getSku(), -request.getQuantity(), userId);

        // Create sales history record
        SalesHistory salesHistory = SalesHistory.builder()
                .sku(request.getSku())
                .quantity(request.getQuantity())
                .saleDate(Timestamp.valueOf(request.getSaleDate()))
                .salePrice(request.getSalePrice())
                .channel(request.getChannel())
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .createdAt(TimestampUtils.now())
                .createdBy(userId)
                .build();

        SalesHistory savedSalesHistory = salesHistoryRepository.save(salesHistory);

        // Send Kafka event
        SalesEvent event = SalesEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .sku(savedSalesHistory.getSku())
                .productName(inventory.getName())
                .quantity(savedSalesHistory.getQuantity())
                .salePrice(savedSalesHistory.getSalePrice())
                .channel(savedSalesHistory.getChannel())
                .orderId(savedSalesHistory.getOrderId())
                .customerId(savedSalesHistory.getCustomerId())
                .userId(userId)
                .saleDate(savedSalesHistory.getSaleDate().toLocalDateTime())
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendSalesEvent(event);

        return mapToDTO(savedSalesHistory);
    }

    @Override
    @Transactional
    public void deleteSalesHistory(Long id) {
        log.info("Deleting sales history with id: {}", id);

        SalesHistory salesHistory = salesHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales history not found with id: " + id));

        // We don't send a Kafka event for deletion as sales history records are generally immutable
        salesHistoryRepository.delete(salesHistory);
    }

    @Override
    public Map<String, Integer> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        log.info("Fetching top {} selling products between {} and {}", limit, startDate, endDate);

        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);

        List<Object[]> results = salesHistoryRepository.findTopSellingProductsBetweenDates(start, end);

        Map<String, Integer> topProducts = new HashMap<>();
        int count = 0;

        for (Object[] result : results) {
            if (count >= limit) break;

            String sku = (String) result[0];
            Long quantity = (Long) result[1];

            topProducts.put(sku, quantity.intValue());
            count++;
        }

        return topProducts;
    }

    private SalesHistoryDTO mapToDTO(SalesHistory salesHistory) {
        String productName = "";

        // Try to get the product name from inventory
        try {
            Inventory inventory = inventoryRepository.findBySku(salesHistory.getSku()).orElse(null);
            if (inventory != null) {
                productName = inventory.getName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch product name for SKU: {}", salesHistory.getSku(), e);
        }

        return SalesHistoryDTO.builder()
                .id(salesHistory.getId())
                .sku(salesHistory.getSku())
                .quantity(salesHistory.getQuantity())
                .saleDate(salesHistory.getSaleDate() != null ? salesHistory.getSaleDate().toLocalDateTime() : null)
                .salePrice(salesHistory.getSalePrice())
                .channel(salesHistory.getChannel())
                .orderId(salesHistory.getOrderId())
                .customerId(salesHistory.getCustomerId())
                .createdAt(salesHistory.getCreatedAt() != null ? salesHistory.getCreatedAt().toLocalDateTime() : null)
                .createdBy(salesHistory.getCreatedBy())
                .productName(productName)
                .build();
    }
}
