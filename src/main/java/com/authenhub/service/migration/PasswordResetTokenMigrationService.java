package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.PasswordResetToken;
import com.authenhub.repository.PasswordResetTokenRepository;
import com.authenhub.repository.jpa.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetTokenMigrationService implements MigrationService {

    private final PasswordResetTokenRepository mongoRepository;
    private final PasswordResetTokenJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating password reset tokens from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.PasswordResetToken> tokens = mongoRepository.findAll();
        log.info("Found {} password reset tokens in MongoDB", tokens.size());
        
        List<PasswordResetToken> jpaTokens = tokens.stream()
                .map(PasswordResetToken::fromMongo)
                .toList();
        
        jpaRepository.saveAll(jpaTokens);
        
        log.info("Successfully migrated {} password reset tokens from MongoDB to PostgreSQL", tokens.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating password reset tokens from PostgreSQL to MongoDB");
        
        List<PasswordResetToken> tokens = jpaRepository.findAll();
        log.info("Found {} password reset tokens in PostgreSQL", tokens.size());
        
        List<com.authenhub.entity.mongo.PasswordResetToken> mongoTokens = tokens.stream()
                .map(PasswordResetToken::toMongo)
                .toList();
        
        mongoRepository.saveAll(mongoTokens);
        
        log.info("Successfully migrated {} password reset tokens from PostgreSQL to MongoDB", tokens.size());
    }
}
