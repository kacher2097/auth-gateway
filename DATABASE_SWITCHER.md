-# Database Switcher

This document explains how to use the database switcher feature to switch between MongoDB and PostgreSQL databases.

## Overview

The database switcher allows you to easily switch between MongoDB and PostgreSQL databases without changing your code. It provides:

1. Configuration options to specify which database to use
2. Automatic data migration between databases
3. API endpoints to control the database switching process

## Configuration

The database switcher is configured in the `application.yml` file:

```yaml
# Database switcher configuration
database:
  # Type of database to use: mongodb or postgresql
  type: mongodb
  # Enable automatic data migration when switching database types
  enable-auto-migration: false
  # Enable detailed migration logs
  detailed-migration-logs: false
  # Batch size for data migration operations
  migration-batch-size: 100
```

### Configuration Options

- `database.type`: The type of database to use. Possible values are `mongodb` or `postgresql`.
- `database.enable-auto-migration`: Whether to automatically migrate data when the application starts and the database type has changed.
- `database.detailed-migration-logs`: Whether to log detailed information during data migration.
- `database.migration-batch-size`: The batch size for data migration operations.

## Switching Databases

### Method 1: Configuration File

To switch databases, update the `database.type` property in the `application.yml` file:

```yaml
database:
  type: postgresql  # Change to 'mongodb' or 'postgresql'
```

If `enable-auto-migration` is set to `true`, data will be automatically migrated when the application starts.

### Method 2: API Endpoints

You can also switch databases using the API endpoints:

#### Get Current Database Configuration

```
GET /api/admin/database/config
```

Response:
```json
{
  "type": "mongodb",
  "enableAutoMigration": false,
  "detailedMigrationLogs": false,
  "migrationBatchSize": 100,
  "isMongoActive": true,
  "isPostgresActive": false
}
```

#### Update Database Configuration

```
PUT /api/admin/database/config
```

Request Body:
```json
{
  "type": "postgresql",
  "enableAutoMigration": true,
  "detailedMigrationLogs": true,
  "migrationBatchSize": 100
}
```

#### Manually Trigger Data Migration

```
POST /api/admin/database/migrate
```

Response:
```json
{
  "message": "Data migration completed successfully",
  "sourceDatabase": "MongoDB",
  "targetDatabase": "PostgreSQL"
}
```

## Data Migration

Data migration is performed by the `DatabaseMigrationService` and its related services. The migration process:

1. Reads data from the source database
2. Converts it to the target database format
3. Writes it to the target database

The migration is performed in batches to avoid memory issues with large datasets.

## Supported Entities

The following entities are supported for migration:

- User
- Role
- Permission
- FreeProxy
- AccessLog
- PaymentMethod
- PasswordResetToken

## Best Practices

1. Always backup your data before switching databases
2. Test the migration process in a non-production environment first
3. Monitor the migration process using the logs
4. Set `detailed-migration-logs` to `true` for more detailed logs during migration
5. Adjust `migration-batch-size` based on your server's memory capacity

## Troubleshooting

If you encounter issues during migration:

1. Check the logs for error messages
2. Ensure both databases are accessible
3. Verify that the database schemas are compatible
4. Try reducing the `migration-batch-size` if you encounter memory issues
5. Set `detailed-migration-logs` to `true` for more detailed logs
