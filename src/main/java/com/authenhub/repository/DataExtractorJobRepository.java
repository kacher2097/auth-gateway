package com.authenhub.repository;

import com.authenhub.entity.DataExtractorJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataExtractorJobRepository extends JpaRepository<DataExtractorJob, Long> {
    
    List<DataExtractorJob> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<DataExtractorJob> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
}
