package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.AccessLog;
import com.authenhub.repository.AccessLogRepository;
import com.authenhub.repository.jpa.AccessLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogMigrationService implements MigrationService {

    private final AccessLogRepository mongoRepository;
    private final AccessLogJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating access logs from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.AccessLog> logs = mongoRepository.findAll();
        log.info("Found {} access logs in MongoDB", logs.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) logs.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, logs.size());
            List<com.authenhub.entity.mongo.AccessLog> batch = logs.subList(start, end);
            
            List<AccessLog> jpaLogs = batch.stream()
                    .map(AccessLog::fromMongo)
                    .toList();
            
            jpaRepository.saveAll(jpaLogs);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of access logs", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} access logs from MongoDB to PostgreSQL", logs.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating access logs from PostgreSQL to MongoDB");
        
        List<AccessLog> logs = jpaRepository.findAll();
        log.info("Found {} access logs in PostgreSQL", logs.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) logs.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, logs.size());
            List<AccessLog> batch = logs.subList(start, end);
            
            List<com.authenhub.entity.mongo.AccessLog> mongoLogs = batch.stream()
                    .map(AccessLog::toMongo)
                    .toList();
            
            mongoRepository.saveAll(mongoLogs);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of access logs", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} access logs from PostgreSQL to MongoDB", logs.size());
    }
}
