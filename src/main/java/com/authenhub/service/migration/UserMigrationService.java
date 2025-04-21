package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.User;
import com.authenhub.repository.UserRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMigrationService implements MigrationService {

    private final UserRepository mongoRepository;
    private final UserJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating users from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.User> users = mongoRepository.findAll();
        log.info("Found {} users in MongoDB", users.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) users.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, users.size());
            List<com.authenhub.entity.mongo.User> batch = users.subList(start, end);
            
            List<User> jpaUsers = batch.stream()
                    .map(User::fromMongo)
                    .toList();
            
            jpaRepository.saveAll(jpaUsers);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of users", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} users from MongoDB to PostgreSQL", users.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating users from PostgreSQL to MongoDB");
        
        List<User> users = jpaRepository.findAll();
        log.info("Found {} users in PostgreSQL", users.size());
        
        int batchSize = databaseSwitcherConfig.getMigrationBatchSize();
        int totalBatches = (int) Math.ceil((double) users.size() / batchSize);
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, users.size());
            List<User> batch = users.subList(start, end);
            
            List<com.authenhub.entity.mongo.User> mongoUsers = batch.stream()
                    .map(User::toMongo)
                    .toList();
            
            mongoRepository.saveAll(mongoUsers);
            
            if (databaseSwitcherConfig.isDetailedMigrationLogs()) {
                log.info("Migrated batch {}/{} of users", i + 1, totalBatches);
            }
        }
        
        log.info("Successfully migrated {} users from PostgreSQL to MongoDB", users.size());
    }
}
