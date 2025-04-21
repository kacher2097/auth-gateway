package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import com.authenhub.repository.jpa.AccessLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter cho AccessLogRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessLogRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.AccessLog, String> {

    private final AccessLogRepository mongoRepository;
    private final AccessLogJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.AccessLog save(com.authenhub.entity.mongo.AccessLog accessLog) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(accessLog);
        } else {
            AccessLog accessLogJpa = AccessLog.fromMongo(accessLog);
            accessLogJpa = jpaRepository.save(accessLogJpa);
            return accessLogJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.AccessLog> saveAll(Iterable<com.authenhub.entity.mongo.AccessLog> accessLogs) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(accessLogs);
        } else {
            List<AccessLog> accessLogJpas = StreamSupport.stream(accessLogs.spliterator(), false)
                    .map(AccessLog::fromMongo)
                    .collect(Collectors.toList());
            accessLogJpas = jpaRepository.saveAll(accessLogJpas);
            return accessLogJpas.stream()
                    .map(AccessLog::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.AccessLog> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(AccessLog::toMongo);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
                return Optional.empty();
            }
        }
    }

    @Override
    public boolean existsById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.existsById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.existsById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
                return false;
            }
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.AccessLog> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(AccessLog::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.AccessLog> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<AccessLog> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.AccessLog> accessLogs = jpaPage.getContent().stream()
                    .map(AccessLog::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(accessLogs, pageable, jpaPage.getTotalElements());
        }
    }

    @Override
    public long count() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.count();
        } else {
            return jpaRepository.count();
        }
    }

    @Override
    public void deleteById(String id) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
            }
        }
    }

    @Override
    public void delete(com.authenhub.entity.mongo.AccessLog accessLog) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(accessLog);
        } else {
            try {
                Long longId = Long.parseLong(accessLog.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", accessLog.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.AccessLog> accessLogs) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(accessLogs);
        } else {
            List<Long> ids = StreamSupport.stream(accessLogs.spliterator(), false)
                    .map(com.authenhub.entity.mongo.AccessLog::getId)
                    .map(id -> {
                        try {
                            return Long.parseLong(id);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid ID format for PostgreSQL: {}", id);
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            List<AccessLog> accessLogJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(accessLogJpas);
        }
    }

    @Override
    public void deleteAll() {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll();
        } else {
            jpaRepository.deleteAll();
        }
    }

    // Các phương thức đặc biệt của AccessLogRepository

    public long countByDateRange(Timestamp start, Timestamp end) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countByDateRange(start, end);
        } else {
            return jpaRepository.countByDateRange(start, end);
        }
    }

    public long countByEndpointContaining(Timestamp start, Timestamp end, String endpointPattern) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countByEndpointContaining(start, end, endpointPattern);
        } else {
            return jpaRepository.countByEndpointContaining(start, end, endpointPattern);
        }
    }

    public long countByEndpointAndStatusCode(Timestamp start, Timestamp end, String endpoint, int statusCode) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countByEndpointAndStatusCode(start, end, endpoint, statusCode);
        } else {
            return jpaRepository.countByEndpointAndStatusCode(start, end, endpoint, statusCode);
        }
    }

    public List<com.authenhub.entity.mongo.AccessLog> findByEndpointContainingAndTimestampBetween(String endpointPattern, Timestamp start, Timestamp end) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByEndpointContainingAndTimestampBetween(endpointPattern, start, end);
        } else {
            return jpaRepository.findByEndpointContainingAndTimestampBetween(endpointPattern, start, end).stream()
                    .map(AccessLog::toMongo)
                    .collect(Collectors.toList());
        }
    }
}
