package com.authenhub.repository.jpa;

import com.authenhub.entity.FreeProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface FreeProxyJpaRepository extends JpaRepository<FreeProxy, Long> {
    List<FreeProxy> findByIsActiveTrue();
    List<FreeProxy> findByProtocol(String protocol);
    List<FreeProxy> findByCountry(String country);
    List<FreeProxy> findByResponseTimeMsLessThan(int maxResponseTime);
    List<FreeProxy> findByLastCheckedAfter(Timestamp date);
    List<FreeProxy> findByCreatedBy(String userId);
    List<FreeProxy> findByUptimeGreaterThan(double minUptime);
}
