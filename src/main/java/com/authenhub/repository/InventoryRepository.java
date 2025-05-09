package com.authenhub.repository;

import com.authenhub.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findBySku(String sku);
    
    List<Inventory> findByCategory(String category);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity = 0")
    List<Inventory> findOutOfStockItems();
}
