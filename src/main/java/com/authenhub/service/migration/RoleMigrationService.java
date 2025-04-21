package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.Role;
import com.authenhub.repository.RoleRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleMigrationService implements MigrationService {

    private final RoleRepository mongoRepository;
    private final RoleJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating roles from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.Role> roles = mongoRepository.findAll();
        log.info("Found {} roles in MongoDB", roles.size());
        
        List<Role> jpaRoles = roles.stream()
                .map(Role::fromMongo)
                .toList();
        
        jpaRepository.saveAll(jpaRoles);
        
        log.info("Successfully migrated {} roles from MongoDB to PostgreSQL", roles.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating roles from PostgreSQL to MongoDB");
        
        List<Role> roles = jpaRepository.findAll();
        log.info("Found {} roles in PostgreSQL", roles.size());
        
        List<com.authenhub.entity.mongo.Role> mongoRoles = roles.stream()
                .map(Role::toMongo)
                .toList();
        
        mongoRepository.saveAll(mongoRoles);
        
        log.info("Successfully migrated {} roles from PostgreSQL to MongoDB", roles.size());
    }
}
