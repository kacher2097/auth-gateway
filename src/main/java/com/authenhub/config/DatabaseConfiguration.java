package com.authenhub.config;

import com.authenhub.config.datasource.DataSourceManagementPostgre;
import com.authenhub.config.mongo.MongoManagement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfiguration {

    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    /**
     * MongoDB configuration that is only activated when database.type=mongodb
     */
    @Configuration
    @ConditionalOnProperty(name = "database.type", havingValue = "mongodb", matchIfMissing = true)
    @Import(MongoManagement.class)
    public static class MongoDbConfiguration {
        public MongoDbConfiguration() {
            log.info("Initializing MongoDB configuration");
        }
    }

    /**
     * PostgreSQL configuration that is only activated when database.type=postgresql
     */
    @Configuration
    @ConditionalOnProperty(name = "database.type", havingValue = "postgresql")
    @Import(DataSourceManagementPostgre.class)
    public static class PostgreSqlConfiguration {
        public PostgreSqlConfiguration() {
            log.info("Initializing PostgreSQL configuration");
        }
    }
}
