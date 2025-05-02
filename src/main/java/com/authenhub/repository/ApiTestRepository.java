package com.authenhub.repository;

import com.authenhub.entity.ApiTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiTestRepository extends JpaRepository<ApiTest, Long> {
    
    List<ApiTest> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<ApiTest> findByUserIdAndFavoriteIsTrueOrderByCreatedAtDesc(String userId);
    
    List<ApiTest> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
}
