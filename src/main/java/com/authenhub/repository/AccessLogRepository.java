package com.authenhub.repository;

import com.authenhub.dto.AccessLogAggregationDto;
import com.authenhub.entity.AccessLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.sql.Timestamp;
import java.util.List;

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
    List<AccessLogAggregationDto.DailyCount> countByDay(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$browser', count: { $sum: 1 } } }",
        "{ $sort: { 'count': -1 } }"
    })
    List<AccessLogAggregationDto.BrowserCount> countByBrowser(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$deviceType', count: { $sum: 1 } } }",
        "{ $sort: { 'count': -1 } }"
    })
    List<AccessLogAggregationDto.DeviceTypeCount> countByDeviceType(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$endpoint', count: { $sum: 1 }, avgResponseTime: { $avg: '$responseTimeMs' } } }",
        "{ $sort: { 'count': -1 } }",
        "{ $limit: 10 }"
    })
    List<AccessLogAggregationDto.EndpointStats> getTopEndpoints(LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
        "{ $match: { 'timestamp': { $gte: ?0, $lte: ?1 } } }",
        "{ $group: { _id: '$userId', count: { $sum: 1 }, username: { $first: '$username' } } }",
        "{ $sort: { 'count': -1 } }",
        "{ $limit: 10 }"
    })
    List<AccessLogAggregationDto.UserStats> getTopUsers(LocalDateTime start, LocalDateTime end);
    List<AccessLog> findByTimestampBetween(Timestamp start, Timestamp end);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByDateRange(Timestamp start, Timestamp end);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 }, 'endpoint': { $regex: ?2, $options: 'i' } }", count = true)
    long countByEndpointContaining(Timestamp start, Timestamp end, String endpointPattern);

    @Query(value = "{ 'timestamp': { $gte: ?0, $lte: ?1 }, 'endpoint': ?2, 'statusCode': ?3 }", count = true)
    long countByEndpointAndStatusCode(Timestamp start, Timestamp end, String endpoint, int statusCode);

    List<AccessLog> findByEndpointContainingAndTimestampBetween(String endpointPattern, Timestamp start, Timestamp end);
}
