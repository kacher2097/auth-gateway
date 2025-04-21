package com.authenhub.repository;

import com.authenhub.entity.mongo.FreeProxy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface FreeProxyRepository extends MongoRepository<FreeProxy, String> {

    List<FreeProxy> findByIsActiveTrue();

    List<FreeProxy> findByProtocol(String protocol);

    List<FreeProxy> findByCountry(String country);

    @Query("{'responseTimeMs': {$lt: ?0}}")
    List<FreeProxy> findByResponseTimeLessThan(int maxResponseTime);

    @Query("{'lastChecked': {$gt: ?0}}")
    List<FreeProxy> findByLastCheckedAfter(Timestamp date);

    List<FreeProxy> findByCreatedBy(String userId);

    @Query("{'uptime': {$gt: ?0}}")
    List<FreeProxy> findByUptimeGreaterThan(double minUptime);
}
