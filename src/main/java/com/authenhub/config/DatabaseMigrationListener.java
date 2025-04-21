package com.authenhub.config;

import com.authenhub.service.migration.DatabaseMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener to automatically migrate data when the application starts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationListener {

    private final DatabaseSwitcherConfig databaseSwitcherConfig;
    private final DatabaseMigrationService databaseMigrationService;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        if (databaseSwitcherConfig.isEnableAutoMigration()) {
            log.info("Auto migration is enabled. Starting data migration...");
            try {
                databaseMigrationService.migrateData();
                log.info("Auto migration completed successfully");
            } catch (Exception e) {
                log.error("Error during auto migration", e);
            }
        } else {
            log.info("Auto migration is disabled. Skipping data migration.");
        }
    }
}
