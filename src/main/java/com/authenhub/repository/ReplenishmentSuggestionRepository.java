package com.authenhub.repository;

import com.authenhub.entity.ReplenishmentSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplenishmentSuggestionRepository extends JpaRepository<ReplenishmentSuggestion, Long> {
    
    List<ReplenishmentSuggestion> findBySku(String sku);
    
    List<ReplenishmentSuggestion> findByStatus(String status);
    
    List<ReplenishmentSuggestion> findByCreatedBy(String createdBy);
    
    List<ReplenishmentSuggestion> findByApprovedBy(String approvedBy);
}
