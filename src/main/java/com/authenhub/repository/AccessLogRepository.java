package com.authenhub.repository;

import com.authenhub.entity.AccessLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AccessLogRepository extends MongoRepository<AccessLog, String> {
    
    List<AccessLog> findByUserId(String userId);
    
    List<AccessLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$timestamp' } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id': 1 } }"
    })
    List<Map<String, Object>> countByDay(LocalDateTime start, LocalDateTime end);
    
    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$browser', count: { $sum: 1 } } }",
        "{ $sort: { 'count': -1 } }"
    })
    List<Map<String, Object>> countByBrowser(LocalDateTime start, LocalDateTime end);
    
    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$deviceType', count: { $sum: 1 } } }",
        "{ $sort: { 'count': -1 } }"
    })
    List<Map<String, Object>> countByDeviceType(LocalDateTime start, LocalDateTime end);
    
    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$endpoint', count: { $sum: 1 }, avgResponseTime: { $avg: '$responseTimeMs' } } }",
        "{ $sort: { 'count': -1 } }",
        "{ $limit: 10 }"
    })
    List<Map<String, Object>> getTopEndpoints(LocalDateTime start, LocalDateTime end);
    
    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$userId', count: { $sum: 1 }, username: { $first: '$username' } } }",
        "{ $sort: { 'count': -1 } }",
        "{ $limit: 10 }"
    })
    List<Map<String, Object>> getTopUsers(LocalDateTime start, LocalDateTime end);
}
