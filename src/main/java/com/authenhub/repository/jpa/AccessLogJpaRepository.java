package com.authenhub.repository.jpa;

import com.authenhub.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AccessLogJpaRepository extends JpaRepository<AccessLog, Long>, JpaSpecificationExecutor<AccessLog> {
    @Query("SELECT COUNT(a) FROM AccessLog a WHERE a.timestamp >= ?1 AND a.timestamp <= ?2")
    long countByDateRange(Timestamp start, Timestamp end);

    @Query("SELECT COUNT(a) FROM AccessLog a WHERE a.timestamp >= ?1 AND a.timestamp <= ?2 AND a.endpoint LIKE %?3%")
    long countByEndpointContaining(Timestamp start, Timestamp end, String endpointPattern);

    @Query("SELECT COUNT(a) FROM AccessLog a WHERE a.timestamp >= ?1 AND a.timestamp <= ?2 AND a.endpoint = ?3 AND a.statusCode = ?4")
    long countByEndpointAndStatusCode(Timestamp start, Timestamp end, String endpoint, int statusCode);

    List<AccessLog> findByEndpointContainingAndTimestampBetween(String endpointPattern, Timestamp start, Timestamp end);
}
