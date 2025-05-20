package com.authenhub.service.impl;

import com.authenhub.dto.inventory.ApproveReplenishmentRequest;
import com.authenhub.dto.inventory.ReplenishmentSuggestionDTO;
import com.authenhub.dto.kafka.ReplenishmentEvent;
import com.authenhub.entity.Inventory;
import com.authenhub.entity.ReplenishmentSuggestion;
import com.authenhub.entity.SalesHistory;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.InventoryRepository;
import com.authenhub.repository.ReplenishmentSuggestionRepository;
import com.authenhub.repository.SalesHistoryRepository;
import com.authenhub.service.InventoryService;
import com.authenhub.service.ReplenishmentService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplenishmentServiceImpl implements ReplenishmentService {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final ReplenishmentSuggestionRepository replenishmentSuggestionRepository;

    @Override
    public List<ReplenishmentSuggestionDTO> getAllReplenishmentSuggestions() {
        log.info("Fetching all replenishment suggestions");
        return replenishmentSuggestionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReplenishmentSuggestionDTO getReplenishmentSuggestionById(Long id) {
        log.info("Fetching replenishment suggestion with id: {}", id);
        return replenishmentSuggestionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Replenishment suggestion not found with id: " + id));
    }

    @Override
    public List<ReplenishmentSuggestionDTO> getReplenishmentSuggestionsBySku(String sku) {
        log.info("Fetching replenishment suggestions for SKU: {}", sku);
        return replenishmentSuggestionRepository.findBySku(sku).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReplenishmentSuggestionDTO> getReplenishmentSuggestionsByStatus(String status) {
        log.info("Fetching replenishment suggestions with status: {}", status);
        return replenishmentSuggestionRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ReplenishmentSuggestionDTO> generateReplenishmentSuggestions(String userId) {
        log.info("Generating replenishment suggestions");

        // Get all inventory items
        List<Inventory> inventoryItems = inventoryRepository.findAll();

        // Calculate the date range for sales history analysis (last 30 days)
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(30, ChronoUnit.DAYS);

        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);

        // Create suggestions for each inventory item
        List<ReplenishmentSuggestion> suggestions = inventoryItems.stream()
                .map(inventory -> {
                    // Get sales history for this SKU
                    List<SalesHistory> salesHistory = salesHistoryRepository.findBySkuAndSaleDateBetween(
                            inventory.getSku(), start, end);

                    // Calculate average daily sales
                    double totalSales = salesHistory.stream()
                            .mapToInt(SalesHistory::getQuantity)
                            .sum();

                    double avgDailySales = totalSales / 30.0; // Average over 30 days

                    // Calculate suggested order quantity based on current stock and average sales
                    // Formula: (Target stock level for 14 days) - Current stock
                    int targetStock = (int) Math.ceil(avgDailySales * 14); // 2 weeks of stock
                    int currentStock = inventory.getQuantity();
                    int suggestedQuantity = Math.max(0, targetStock - currentStock);

                    // Only create suggestion if we need to order
                    if (suggestedQuantity > 0 || (inventory.getLowStockThreshold() != null && currentStock <= inventory.getLowStockThreshold())) {
                        String reason;

                        if (inventory.getLowStockThreshold() != null && currentStock <= inventory.getLowStockThreshold()) {
                            reason = "Below low stock threshold of " + inventory.getLowStockThreshold();
                            // If no sales but below threshold, suggest minimum order
                            if (suggestedQuantity == 0) {
                                suggestedQuantity = Math.max(1, inventory.getLowStockThreshold() - currentStock);
                            }
                        } else {
                            reason = "Based on average daily sales of " + String.format("%.2f", avgDailySales) + " units";
                        }

                        return ReplenishmentSuggestion.builder()
                                .sku(inventory.getSku())
                                .suggestedQuantity(suggestedQuantity)
                                .reason(reason)
                                .status("PENDING")
                                .createdAt(TimestampUtils.now())
                                .createdBy(userId)
                                .build();
                    }

                    return null;
                })
                .filter(suggestion -> suggestion != null)
                .collect(Collectors.toList());

        // Save all suggestions
        List<ReplenishmentSuggestion> savedSuggestions = replenishmentSuggestionRepository.saveAll(suggestions);

        // Send Kafka events for each suggestion
        for (ReplenishmentSuggestion suggestion : savedSuggestions) {
            // Try to get product name
            String productName = "";
            Inventory inventory = inventoryRepository.findBySku(suggestion.getSku()).orElse(null);
            if (inventory != null) {
                productName = inventory.getName();
            }

            ReplenishmentEvent event = ReplenishmentEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("SUGGESTED")
                    .suggestionId(suggestion.getId())
                    .sku(suggestion.getSku())
                    .productName(productName)
                    .suggestedQuantity(suggestion.getSuggestedQuantity())
                    .reason(suggestion.getReason())
                    .status(suggestion.getStatus())
                    .userId(userId)
                    .timestamp(LocalDateTime.now())
                    .build();

//            kafkaProducerService.sendReplenishmentEvent(event);
        }

        // Convert to DTOs and return
        return savedSuggestions.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReplenishmentSuggestionDTO approveReplenishmentSuggestion(Long id, ApproveReplenishmentRequest request, String userId) {
        log.info("Approving replenishment suggestion with id: {}", id);

        ReplenishmentSuggestion suggestion = replenishmentSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Replenishment suggestion not found with id: " + id));

        if (!"PENDING".equals(suggestion.getStatus())) {
            throw new IllegalStateException("Cannot approve suggestion that is not in PENDING status");
        }

        // Update suggestion
        suggestion.setStatus("APPROVED");
        suggestion.setApprovedQuantity(request.getApprovedQuantity());
        suggestion.setApprovedBy(userId);
        suggestion.setApprovedAt(TimestampUtils.now());
        suggestion.setUpdatedAt(TimestampUtils.now());
        suggestion.setUpdatedBy(userId);

        // If approved quantity is greater than 0, update inventory
        if (request.getApprovedQuantity() > 0) {
            inventoryService.updateInventoryQuantity(suggestion.getSku(), request.getApprovedQuantity(), userId);
        }

        ReplenishmentSuggestion updatedSuggestion = replenishmentSuggestionRepository.save(suggestion);

        // Try to get product name
        String productName = "";
        Inventory inventory = inventoryRepository.findBySku(updatedSuggestion.getSku()).orElse(null);
        if (inventory != null) {
            productName = inventory.getName();
        }

        // Send Kafka event
        ReplenishmentEvent event = ReplenishmentEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("APPROVED")
                .suggestionId(updatedSuggestion.getId())
                .sku(updatedSuggestion.getSku())
                .productName(productName)
                .suggestedQuantity(updatedSuggestion.getSuggestedQuantity())
                .approvedQuantity(updatedSuggestion.getApprovedQuantity())
                .reason(updatedSuggestion.getReason())
                .status(updatedSuggestion.getStatus())
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendReplenishmentEvent(event);

        return mapToDTO(updatedSuggestion);
    }

    @Override
    @Transactional
    public ReplenishmentSuggestionDTO rejectReplenishmentSuggestion(Long id, String userId) {
        log.info("Rejecting replenishment suggestion with id: {}", id);

        ReplenishmentSuggestion suggestion = replenishmentSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Replenishment suggestion not found with id: " + id));

        if (!"PENDING".equals(suggestion.getStatus())) {
            throw new IllegalStateException("Cannot reject suggestion that is not in PENDING status");
        }

        // Update suggestion
        suggestion.setStatus("REJECTED");
        suggestion.setUpdatedAt(TimestampUtils.now());
        suggestion.setUpdatedBy(userId);

        ReplenishmentSuggestion updatedSuggestion = replenishmentSuggestionRepository.save(suggestion);

        // Try to get product name
        String productName = "";
        Inventory inventory = inventoryRepository.findBySku(updatedSuggestion.getSku()).orElse(null);
        if (inventory != null) {
            productName = inventory.getName();
        }

        // Send Kafka event
        ReplenishmentEvent event = ReplenishmentEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("REJECTED")
                .suggestionId(updatedSuggestion.getId())
                .sku(updatedSuggestion.getSku())
                .productName(productName)
                .suggestedQuantity(updatedSuggestion.getSuggestedQuantity())
                .reason(updatedSuggestion.getReason())
                .status(updatedSuggestion.getStatus())
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendReplenishmentEvent(event);

        return mapToDTO(updatedSuggestion);
    }

    private ReplenishmentSuggestionDTO mapToDTO(ReplenishmentSuggestion suggestion) {
        String productName = "";
        Integer currentStock = null;

        // Try to get the product name and current stock from inventory
        try {
            Inventory inventory = inventoryRepository.findBySku(suggestion.getSku()).orElse(null);
            if (inventory != null) {
                productName = inventory.getName();
                currentStock = inventory.getQuantity();
            }
        } catch (Exception e) {
            log.warn("Could not fetch product details for SKU: {}", suggestion.getSku(), e);
        }

        return ReplenishmentSuggestionDTO.builder()
                .id(suggestion.getId())
                .sku(suggestion.getSku())
                .suggestedQuantity(suggestion.getSuggestedQuantity())
                .reason(suggestion.getReason())
                .status(suggestion.getStatus())
                .approvedQuantity(suggestion.getApprovedQuantity())
                .approvedBy(suggestion.getApprovedBy())
                .approvedAt(suggestion.getApprovedAt() != null ? suggestion.getApprovedAt().toLocalDateTime() : null)
                .createdAt(suggestion.getCreatedAt() != null ? suggestion.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(suggestion.getUpdatedAt() != null ? suggestion.getUpdatedAt().toLocalDateTime() : null)
                .createdBy(suggestion.getCreatedBy())
                .updatedBy(suggestion.getUpdatedBy())
                .productName(productName)
                .currentStock(currentStock)
                .build();
    }
}
