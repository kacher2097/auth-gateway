package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.Permission;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionMigrationService implements MigrationService {

    private final PermissionRepository mongoRepository;
    private final PermissionJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating permissions from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.Permission> permissions = mongoRepository.findAll();
        log.info("Found {} permissions in MongoDB", permissions.size());
        
        List<Permission> jpaPermissions = permissions.stream()
                .map(Permission::fromMongo)
                .toList();
        
        jpaRepository.saveAll(jpaPermissions);
        
        log.info("Successfully migrated {} permissions from MongoDB to PostgreSQL", permissions.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating permissions from PostgreSQL to MongoDB");
        
        List<Permission> permissions = jpaRepository.findAll();
        log.info("Found {} permissions in PostgreSQL", permissions.size());
        
        List<com.authenhub.entity.mongo.Permission> mongoPermissions = permissions.stream()
                .map(Permission::toMongo)
                .toList();
        
        mongoRepository.saveAll(mongoPermissions);
        
        log.info("Successfully migrated {} permissions from PostgreSQL to MongoDB", permissions.size());
    }
}
