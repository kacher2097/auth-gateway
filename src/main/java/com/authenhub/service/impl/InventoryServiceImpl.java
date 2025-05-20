package com.authenhub.service.impl;

import com.authenhub.dto.inventory.CreateInventoryRequest;
import com.authenhub.dto.inventory.InventoryDTO;
import com.authenhub.dto.inventory.UpdateInventoryRequest;
import com.authenhub.dto.kafka.InventoryEvent;
import com.authenhub.entity.Inventory;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.InventoryRepository;
import com.authenhub.service.InventoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
//    private final KafkaProducerService kafkaProducerService;

    @Override
    public List<InventoryDTO> getAllInventory() {
        log.info("Fetching all inventory items");
        return inventoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO getInventoryById(Long id) {
        log.info("Fetching inventory item with id: {}", id);
        return inventoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
    }

    @Override
    public InventoryDTO getInventoryBySku(String sku) {
        log.info("Fetching inventory item with SKU: {}", sku);
        return inventoryRepository.findBySku(sku)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + sku));
    }

    @Override
    @Transactional
    public InventoryDTO createInventory(CreateInventoryRequest request, String userId) {
        log.info("Creating new inventory item with SKU: {}", request.getSku());

        // Check if SKU already exists
        if (inventoryRepository.findBySku(request.getSku()).isPresent()) {
            throw new IllegalArgumentException("Inventory item with SKU " + request.getSku() + " already exists");
        }

        Inventory inventory = Inventory.builder()
                .sku(request.getSku())
                .name(request.getName())
                .quantity(request.getQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .category(request.getCategory())
                .description(request.getDescription())
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .supplier(request.getSupplier())
                .location(request.getLocation())
                .createdAt(TimestampUtils.now())
                .createdBy(userId)
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        // Send Kafka event
        InventoryEvent event = InventoryEvent.builder()
                .eventType("CREATED")
                .sku(savedInventory.getSku())
                .productName(savedInventory.getName())
                .newQuantity(savedInventory.getQuantity())
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendInventoryEvent(event);

        return mapToDTO(savedInventory);
    }

    @Override
    @Transactional
    public InventoryDTO updateInventory(String sku, UpdateInventoryRequest request, String userId) {
        log.info("Updating inventory item with SKU: {}", sku);

        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + sku));

        if (request.getName() != null) {
            inventory.setName(request.getName());
        }

        if (request.getQuantity() != null) {
            inventory.setQuantity(request.getQuantity());
        }

        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }

        if (request.getCategory() != null) {
            inventory.setCategory(request.getCategory());
        }

        if (request.getDescription() != null) {
            inventory.setDescription(request.getDescription());
        }

        if (request.getCostPrice() != null) {
            inventory.setCostPrice(request.getCostPrice());
        }

        if (request.getSellingPrice() != null) {
            inventory.setSellingPrice(request.getSellingPrice());
        }

        if (request.getSupplier() != null) {
            inventory.setSupplier(request.getSupplier());
        }

        if (request.getLocation() != null) {
            inventory.setLocation(request.getLocation());
        }

        inventory.setUpdatedAt(TimestampUtils.now());
        inventory.setUpdatedBy(userId);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        // Send Kafka event
        InventoryEvent event = InventoryEvent.builder()
                .eventType("UPDATED")
                .sku(updatedInventory.getSku())
                .productName(updatedInventory.getName())
                .newQuantity(updatedInventory.getQuantity())
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendInventoryEvent(event);

        return mapToDTO(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(String sku) {
        log.info("Deleting inventory item with SKU: {}", sku);

        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + sku));

        // Send Kafka event before deleting
        InventoryEvent event = InventoryEvent.builder()
                .eventType("DELETED")
                .sku(inventory.getSku())
                .productName(inventory.getName())
                .oldQuantity(inventory.getQuantity())
                .userId("system")
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendInventoryEvent(event);

        inventoryRepository.delete(inventory);
    }

    @Override
    public List<InventoryDTO> getLowStockItems() {
        log.info("Fetching low stock inventory items");
        return inventoryRepository.findLowStockItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> getOutOfStockItems() {
        log.info("Fetching out of stock inventory items");
        return inventoryRepository.findOutOfStockItems().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryDTO updateInventoryQuantity(String sku, Integer quantityChange, String userId) {
        log.info("Updating inventory quantity for SKU: {} by {}", sku, quantityChange);

        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + sku));

        int oldQuantity = inventory.getQuantity();
        int newQuantity = oldQuantity + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Cannot reduce inventory below zero. Current quantity: " + inventory.getQuantity() + ", Requested change: " + quantityChange);
        }

        inventory.setQuantity(newQuantity);
        inventory.setUpdatedAt(TimestampUtils.now());
        inventory.setUpdatedBy(userId);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        // Send Kafka event
        InventoryEvent event = InventoryEvent.builder()
                .eventType("QUANTITY_CHANGED")
                .sku(updatedInventory.getSku())
                .productName(updatedInventory.getName())
                .oldQuantity(oldQuantity)
                .newQuantity(newQuantity)
                .quantityChange(quantityChange)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

//        kafkaProducerService.sendInventoryEvent(event);

        return mapToDTO(updatedInventory);
    }

    private InventoryDTO mapToDTO(Inventory inventory) {
        boolean isLowStock = inventory.getLowStockThreshold() != null &&
                inventory.getQuantity() <= inventory.getLowStockThreshold();

        return InventoryDTO.builder()
                .id(inventory.getId())
                .sku(inventory.getSku())
                .name(inventory.getName())
                .quantity(inventory.getQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .category(inventory.getCategory())
                .description(inventory.getDescription())
                .costPrice(inventory.getCostPrice())
                .sellingPrice(inventory.getSellingPrice())
                .supplier(inventory.getSupplier())
                .location(inventory.getLocation())
                .createdAt(inventory.getCreatedAt() != null ? inventory.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(inventory.getUpdatedAt() != null ? inventory.getUpdatedAt().toLocalDateTime() : null)
                .createdBy(inventory.getCreatedBy())
                .updatedBy(inventory.getUpdatedBy())
                .isLowStock(isLowStock)
                .build();
    }
}
