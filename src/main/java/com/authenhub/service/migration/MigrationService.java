package com.authenhub.service.migration;

/**
 * Interface for entity migration services.
 */
public interface MigrationService {
    
    /**
     * Migrate data from MongoDB to PostgreSQL.
     */
    void migrateFromMongoToPostgres();
    
    /**
     * Migrate data from PostgreSQL to MongoDB.
     */
    void migrateFromPostgresToMongo();
}
