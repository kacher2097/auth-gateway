package com.authenhub.repository;

import com.authenhub.entity.AffiliateScraperJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffiliateScraperJobRepository extends JpaRepository<AffiliateScraperJob, Long> {
    
    List<AffiliateScraperJob> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<AffiliateScraperJob> findByUserIdAndPlatformOrderByCreatedAtDesc(String userId, String platform);
    
    List<AffiliateScraperJob> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
}
