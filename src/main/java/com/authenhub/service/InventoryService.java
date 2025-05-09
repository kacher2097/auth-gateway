package com.authenhub.service;

import com.authenhub.dto.inventory.CreateInventoryRequest;
import com.authenhub.dto.inventory.InventoryDTO;
import com.authenhub.dto.inventory.UpdateInventoryRequest;

import java.util.List;

public interface InventoryService {
    
    List<InventoryDTO> getAllInventory();
    
    InventoryDTO getInventoryById(Long id);
    
    InventoryDTO getInventoryBySku(String sku);
    
    InventoryDTO createInventory(CreateInventoryRequest request, String userId);
    
    InventoryDTO updateInventory(String sku, UpdateInventoryRequest request, String userId);
    
    void deleteInventory(String sku);
    
    List<InventoryDTO> getLowStockItems();
    
    List<InventoryDTO> getOutOfStockItems();
    
    InventoryDTO updateInventoryQuantity(String sku, Integer quantityChange, String userId);
}
