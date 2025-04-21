package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.FreeProxy;
import com.authenhub.repository.FreeProxyRepository;
import com.authenhub.repository.jpa.FreeProxyJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreeProxyMigrationService implements MigrationService {

    private final FreeProxyRepository mongoRepository;
    private final FreeProxyJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating free proxies from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.FreeProxy> proxies = mongoRepository.findAll();
        log.info("Found {} free proxies in MongoDB", proxies.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) proxies.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, proxies.size());
            List<com.authenhub.entity.mongo.FreeProxy> batch = proxies.subList(start, end);
            
            List<FreeProxy> jpaProxies = batch.stream()
                    .map(FreeProxy::fromMongo)
                    .toList();
            
            jpaRepository.saveAll(jpaProxies);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of free proxies", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} free proxies from MongoDB to PostgreSQL", proxies.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating free proxies from PostgreSQL to MongoDB");
        
        List<FreeProxy> proxies = jpaRepository.findAll();
        log.info("Found {} free proxies in PostgreSQL", proxies.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) proxies.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, proxies.size());
            List<FreeProxy> batch = proxies.subList(start, end);
            
            List<com.authenhub.entity.mongo.FreeProxy> mongoProxies = batch.stream()
                    .map(FreeProxy::toMongo)
                    .toList();
            
            mongoRepository.saveAll(mongoProxies);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of free proxies", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} free proxies from PostgreSQL to MongoDB", proxies.size());
    }
}
