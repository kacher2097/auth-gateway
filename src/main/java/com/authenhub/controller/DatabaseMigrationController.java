package com.authenhub.controller;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.service.migration.DatabaseMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for database migration operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/database")
public class DatabaseMigrationController {

    private final DatabaseMigrationService databaseMigrationService;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    /**
     * Get current database configuration.
     */
    @GetMapping("/config")
//    @PreAuthorize("hasAuthority('settings:read')")
    public ResponseEntity<Map<String, Object>> getDatabaseConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("type", databaseSwitcherConfig.getType());
        config.put("enableAutoMigration", databaseSwitcherConfig.isEnableAutoMigration());
        config.put("detailedMigrationLogs", databaseSwitcherConfig.isDetailedMigrationLogs());
        config.put("migrationBatchSize", databaseSwitcherConfig.getMigrationBatchSize());
        config.put("isMongoActive", databaseSwitcherConfig.isMongoActive());
        config.put("isPostgresActive", databaseSwitcherConfig.isPostgresActive());
        
        return ResponseEntity.ok(config);
    }

    /**
     * Update database configuration.
     */
    @PutMapping("/config")
//    @PreAuthorize("hasAuthority('settings:update')")
    public ResponseEntity<Map<String, Object>> updateDatabaseConfig(@RequestBody DatabaseSwitcherConfig config) {
        databaseSwitcherConfig.setType(config.getType());
        databaseSwitcherConfig.setEnableAutoMigration(config.isEnableAutoMigration());
        databaseSwitcherConfig.setDetailedMigrationLogs(config.isDetailedMigrationLogs());
        databaseSwitcherConfig.setMigrationBatchSize(config.getMigrationBatchSize());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Database configuration updated successfully");
        response.put("config", databaseSwitcherConfig);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Trigger data migration.
     */
    @PostMapping("/migrate")
//    @PreAuthorize("hasAuthority('settings:update')")
    public ResponseEntity<Map<String, Object>> migrateData() {
        log.info("Manual data migration triggered");
        
        try {
            databaseMigrationService.migrateData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data migration completed successfully");
            response.put("sourceDatabase", databaseSwitcherConfig.isMongoActive() ? "PostgreSQL" : "MongoDB");
            response.put("targetDatabase", databaseSwitcherConfig.isMongoActive() ? "MongoDB" : "PostgreSQL");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during data migration", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data migration failed");
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
