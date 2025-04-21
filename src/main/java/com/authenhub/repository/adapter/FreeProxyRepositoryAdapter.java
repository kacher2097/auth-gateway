package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.FreeProxy;
import com.authenhub.repository.FreeProxyRepository;
import com.authenhub.repository.jpa.FreeProxyJpaRepository;
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
 * Adapter cho FreeProxyRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FreeProxyRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.FreeProxy, String> {

    private final FreeProxyRepository mongoRepository;
    private final FreeProxyJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.FreeProxy save(com.authenhub.entity.mongo.FreeProxy proxy) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(proxy);
        } else {
            FreeProxy proxyJpa = FreeProxy.fromMongo(proxy);
            proxyJpa = jpaRepository.save(proxyJpa);
            return proxyJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.FreeProxy> saveAll(Iterable<com.authenhub.entity.mongo.FreeProxy> proxies) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(proxies);
        } else {
            List<FreeProxy> proxyJpas = StreamSupport.stream(proxies.spliterator(), false)
                    .map(FreeProxy::fromMongo)
                    .collect(Collectors.toList());
            proxyJpas = jpaRepository.saveAll(proxyJpas);
            return proxyJpas.stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.FreeProxy> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(FreeProxy::toMongo);
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
    public List<com.authenhub.entity.mongo.FreeProxy> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.FreeProxy> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<FreeProxy> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.FreeProxy> proxies = jpaPage.getContent().stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(proxies, pageable, jpaPage.getTotalElements());
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
    public void delete(com.authenhub.entity.mongo.FreeProxy proxy) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(proxy);
        } else {
            try {
                Long longId = Long.parseLong(proxy.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", proxy.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.FreeProxy> proxies) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(proxies);
        } else {
            List<Long> ids = StreamSupport.stream(proxies.spliterator(), false)
                    .map(com.authenhub.entity.mongo.FreeProxy::getId)
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
            
            List<FreeProxy> proxyJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(proxyJpas);
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

    // Các phương thức đặc biệt của FreeProxyRepository

    public List<com.authenhub.entity.mongo.FreeProxy> findByIsActiveTrue() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByIsActiveTrue();
        } else {
            return jpaRepository.findByIsActiveTrue().stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByProtocol(String protocol) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByProtocol(protocol);
        } else {
            return jpaRepository.findByProtocol(protocol).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByCountry(String country) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByCountry(country);
        } else {
            return jpaRepository.findByCountry(country).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByResponseTimeLessThan(int maxResponseTime) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByResponseTimeLessThan(maxResponseTime);
        } else {
            return jpaRepository.findByResponseTimeMsLessThan(maxResponseTime).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByLastCheckedAfter(Timestamp date) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByLastCheckedAfter(date);
        } else {
            return jpaRepository.findByLastCheckedAfter(date).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByCreatedBy(String userId) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByCreatedBy(userId);
        } else {
            return jpaRepository.findByCreatedBy(userId).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.FreeProxy> findByUptimeGreaterThan(double minUptime) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByUptimeGreaterThan(minUptime);
        } else {
            return jpaRepository.findByUptimeGreaterThan(minUptime).stream()
                    .map(FreeProxy::toMongo)
                    .collect(Collectors.toList());
        }
    }
}
