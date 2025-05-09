package com.authenhub.repository;

import com.authenhub.entity.SalesHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface SalesHistoryRepository extends JpaRepository<SalesHistory, Long> {
    
    List<SalesHistory> findBySku(String sku);
    
    List<SalesHistory> findBySkuAndSaleDateBetween(String sku, Timestamp startDate, Timestamp endDate);
    
    @Query("SELECT s FROM SalesHistory s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
    List<SalesHistory> findSalesBetweenDates(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
    
    @Query("SELECT s.sku, SUM(s.quantity) as totalQuantity FROM SalesHistory s WHERE s.saleDate BETWEEN :startDate AND :endDate GROUP BY s.sku ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProductsBetweenDates(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
}
