package com.authenhub.service.migration;

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
