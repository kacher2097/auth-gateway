package com.authenhub.repository;

import com.authenhub.entity.AccessLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface AccessLogRepository extends MongoRepository<AccessLog, String> {

    List<AccessLog> findByUserId(String userId);

    List<AccessLog> findByTimestampBetween(Timestamp start, Timestamp end);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByDateRange(Timestamp start, Timestamp end);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 }, 'endpoint': { $regex: ?2, $options: 'i' } }", count = true)
    long countByEndpointContaining(Timestamp start, Timestamp end, String endpointPattern);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 }, 'endpoint': ?2, 'statusCode': ?3 }", count = true)
    long countByEndpointAndStatusCode(Timestamp start, Timestamp end, String endpoint, int statusCode);

    List<AccessLog> findByEndpointContainingAndTimestampBetween(String endpointPattern, Timestamp start, Timestamp end);
}
