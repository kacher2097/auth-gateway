package com.authenhub.service;

import com.authenhub.dto.inventory.ApproveReplenishmentRequest;
import com.authenhub.dto.inventory.ReplenishmentSuggestionDTO;

import java.util.List;

public interface ReplenishmentService {
    
    List<ReplenishmentSuggestionDTO> getAllReplenishmentSuggestions();
    
    ReplenishmentSuggestionDTO getReplenishmentSuggestionById(Long id);
    
    List<ReplenishmentSuggestionDTO> getReplenishmentSuggestionsBySku(String sku);
    
    List<ReplenishmentSuggestionDTO> getReplenishmentSuggestionsByStatus(String status);
    
    List<ReplenishmentSuggestionDTO> generateReplenishmentSuggestions(String userId);
    
    ReplenishmentSuggestionDTO approveReplenishmentSuggestion(Long id, ApproveReplenishmentRequest request, String userId);
    
    ReplenishmentSuggestionDTO rejectReplenishmentSuggestion(Long id, String userId);
}
