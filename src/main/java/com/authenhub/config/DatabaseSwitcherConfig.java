package com.authenhub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "database")
public class DatabaseSwitcherConfig {

    private String type = "mongodb";
    private boolean enableAutoMigration = false;
    private boolean detailedMigrationLogs = false;
    private int migrationBatchSize = 100;

    public boolean isMongoActive() {
        return "mongodb".equalsIgnoreCase(type);
    }

    public boolean isPostgresActive() {
        return "postgresql".equalsIgnoreCase(type);
    }
}
