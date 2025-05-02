package com.authenhub.repository;

import com.authenhub.entity.BlogCrawlerJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCrawlerJobRepository extends JpaRepository<BlogCrawlerJob, Long> {
    
    List<BlogCrawlerJob> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<BlogCrawlerJob> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);
}
