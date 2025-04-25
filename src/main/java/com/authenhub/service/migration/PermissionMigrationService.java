package com.authenhub.service.migration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionMigrationService implements MigrationService {


    @Override
    public void migrateFromMongoToPostgres() {

    }

    @Override
    public void migrateFromPostgresToMongo() {

    }
}
