package com.authenhub.repository;

import com.authenhub.entity.WebCrawlerJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface WebCrawlerJobRepository extends JpaRepository<WebCrawlerJob, Long> {

    List<WebCrawlerJob> findByUserIdOrderByCreatedAtDesc(String userId);

    List<WebCrawlerJob> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);

    List<WebCrawlerJob> findByStatusAndNextRunAtLessThanEqual(String status, Timestamp dateTime);
}
