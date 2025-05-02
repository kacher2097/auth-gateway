package com.authenhub.repository;

import com.authenhub.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByTrackedProductIdOrderByTimestampDesc(Long trackedProductId);

    List<PriceHistory> findByTrackedProductIdAndTimestampBetweenOrderByTimestamp(
            Long trackedProductId, Timestamp startDate, Timestamp endDate);
}
