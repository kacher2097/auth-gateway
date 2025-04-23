package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for migrating data between MongoDB and PostgreSQL.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseMigrationService {

    private final DatabaseSwitcherConfig databaseSwitcherConfig;
    private final UserMigrationService userMigrationService;
    private final RoleMigrationService roleMigrationService;
    private final PermissionMigrationService permissionMigrationService;
    private final FreeProxyMigrationService freeProxyMigrationService;
    private final AccessLogMigrationService accessLogMigrationService;
    private final PaymentMethodMigrationService paymentMethodMigrationService;
    private final PasswordResetTokenMigrationService passwordResetTokenMigrationService;

    /**
     * Migrate data from MongoDB to PostgreSQL.
     */
    public void migrateFromMongoToPostgres() {
        log.info("Starting migration from MongoDB to PostgreSQL");

        permissionMigrationService.migrateFromMongoToPostgres();
        roleMigrationService.migrateFromMongoToPostgres();
        userMigrationService.migrateFromMongoToPostgres();
        freeProxyMigrationService.migrateFromMongoToPostgres();
        accessLogMigrationService.migrateFromMongoToPostgres();
        paymentMethodMigrationService.migrateFromMongoToPostgres();
        passwordResetTokenMigrationService.migrateFromMongoToPostgres();
        
        log.info("Migration from MongoDB to PostgreSQL completed");
    }

    /**
     * Migrate data from PostgreSQL to MongoDB.
     */
    public void migrateFromPostgresToMongo() {
        log.info("Starting migration from PostgreSQL to MongoDB");

        permissionMigrationService.migrateFromPostgresToMongo();
        roleMigrationService.migrateFromPostgresToMongo();
        userMigrationService.migrateFromPostgresToMongo();
        freeProxyMigrationService.migrateFromPostgresToMongo();
        accessLogMigrationService.migrateFromPostgresToMongo();
        paymentMethodMigrationService.migrateFromPostgresToMongo();
        passwordResetTokenMigrationService.migrateFromPostgresToMongo();
        
        log.info("Migration from PostgreSQL to MongoDB completed");
    }

    /**
     * Migrate data based on the current database type.
     */
    public void migrateData() {
        if (databaseSwitcherConfig.isMongoActive()) {
            migrateFromPostgresToMongo();
        } else if (databaseSwitcherConfig.isPostgresActive()) {
            migrateFromMongoToPostgres();
        } else {
            log.error("Unknown database type: {}", databaseSwitcherConfig.getType());
        }
    }
}
