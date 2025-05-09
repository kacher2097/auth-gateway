package com.authenhub.service;

import com.authenhub.dto.inventory.CreateSalesHistoryRequest;
import com.authenhub.dto.inventory.SalesHistoryDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SalesHistoryService {
    
    List<SalesHistoryDTO> getAllSalesHistory();
    
    SalesHistoryDTO getSalesHistoryById(Long id);
    
    List<SalesHistoryDTO> getSalesHistoryBySku(String sku);
    
    List<SalesHistoryDTO> getSalesHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    SalesHistoryDTO createSalesHistory(CreateSalesHistoryRequest request, String userId);
    
    void deleteSalesHistory(Long id);
    
    Map<String, Integer> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);
}
