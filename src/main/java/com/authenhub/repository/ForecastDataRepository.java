package com.authenhub.repository;

import com.authenhub.entity.ForecastData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ForecastDataRepository extends JpaRepository<ForecastData, Long> {
    
    List<ForecastData> findBySku(String sku);
    
    List<ForecastData> findBySkuAndForecastDateBetween(String sku, Timestamp startDate, Timestamp endDate);
    
    @Query("SELECT f FROM ForecastData f WHERE f.forecastDate BETWEEN :startDate AND :endDate ORDER BY f.forecastDate ASC")
    List<ForecastData> findForecastsBetweenDates(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
}
