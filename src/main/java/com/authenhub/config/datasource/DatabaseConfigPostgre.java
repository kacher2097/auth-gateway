package com.authenhub.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "datasource")
public class DatabaseConfigPostgre {

    private String driver;
    private String url;
    private String username;
    private String password;

    @Value("${datasource.idle-timeout}")
    private Long idleTimeout;

    @Value("${datasource.max-lifetime}")
    private Long maxLifetime;

    @Value("${datasource.connection-timeout}")
    private Long connectionTimeout;

    @Value("${datasource.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${datasource.minimum-idle}")
    private int minimumIdle;

    @Value("${datasource.max-attempts-retry}")
    private int maxAttemptsRetry;

    @Value("${datasource.max-delay-retry}")
    private Long maxDelayRetry;

    @Value("${datasource.batch-size:100}")
    private int batchSize;

    @Value("${datasource.entity-bean-package}")
    private String entityBeanPackage;

    @Value("${datasource.connection-test-query}")
    private String testConnectionQuery;

    @Value("${datasource.pool-name}")
    private String poolName;

    @Value("${datasource.database-platform}")
    private String databasePlatform;

    @Value("${datasource.show-sql}")
    private Boolean showSql;

    @Value("${datasource.format_sql}")
    private Boolean formatSql;

    @Value("${datasource.dialect:org.hibernate.dialect.PostgreSQLDialect}")
    private String dialect;

    @Value("${datasource.order_inserts:true}")
    private Boolean orderInsertsDefault;

    @Value("${datasource.order_updates:true}")
    private Boolean orderUpdatesDefault;

    @Value("${datasource.batch-versioned-data:false}")
    private Boolean batchVersionedDataDefault;

    @Value("${datasource.param-null-passing:false}")
    private Boolean paramNullPassingDefault;

    @Value("${datasource.order_inserts:true}")
    private Boolean orderInserts;
    @Value("${datasource.order_updates:true}")
    private Boolean orderUpdates;
    @Value("${datasource.batch-versioned-data:false}")
    private Boolean batchVersionedData;
    @Value("${datasource.param-null-passing:false}")
    private Boolean paramNullPassing;

}
