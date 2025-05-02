package com.authenhub.repository;

import com.authenhub.entity.TrackedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackedProductRepository extends JpaRepository<TrackedProduct, Long> {
    
    List<TrackedProduct> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<TrackedProduct> findByUserIdAndPlatformOrderByCreatedAtDesc(String userId, String platform);
    
    Optional<TrackedProduct> findByUserIdAndProductIdAndPlatform(String userId, String productId, String platform);
    
    List<TrackedProduct> findByNotifyOnPriceChangeIsTrue();
    
//    List<TrackedProduct> findByNotifyOnTargetPriceIsTrueAndCurrentPriceLessThanEqualTargetPrice();
}
