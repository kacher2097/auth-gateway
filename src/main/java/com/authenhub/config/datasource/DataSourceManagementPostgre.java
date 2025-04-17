package com.authenhub.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "com.authenhub.repository")
@RequiredArgsConstructor
public class DataSourceManagementPostgre {

    private final DatabaseConfigPostgre databaseConfigPostgre;

    @Bean
    @Primary
    public DataSource getDataSource() {
        String url = databaseConfigPostgre.getUrl();

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(databaseConfigPostgre.getDriver());
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(databaseConfigPostgre.getUsername());
        dataSource.setPassword(databaseConfigPostgre.getPassword());
        dataSource.setConnectionTimeout(databaseConfigPostgre.getConnectionTimeout());
        dataSource.setIdleTimeout(databaseConfigPostgre.getIdleTimeout());
        dataSource.setMaximumPoolSize(databaseConfigPostgre.getMaxPoolSize());
        dataSource.setPoolName(databaseConfigPostgre.getPoolName());
        dataSource.setConnectionTestQuery(databaseConfigPostgre.getTestConnectionQuery());
        dataSource.setMinimumIdle(databaseConfigPostgre.getMinimumIdle());
        dataSource.setMaximumPoolSize(databaseConfigPostgre.getMaxPoolSize());
        dataSource.setMaxLifetime(databaseConfigPostgre.getMaxLifetime());

        log.info("Connected database via URL = [{}].", url);
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(getDataSource());
        entityManagerFactoryBean.setPackagesToScan(databaseConfigPostgre.getEntityBeanPackage());
        entityManagerFactoryBean.setPersistenceUnitName("EntityManager");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform(databaseConfigPostgre.getDatabasePlatform());
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.proc.param_null_passing", databaseConfigPostgre.getParamNullPassing().toString());
        properties.put("hibernate.jdbc.batch_size", databaseConfigPostgre.getBatchSize());
        properties.put("hibernate.order_inserts", databaseConfigPostgre.getOrderInserts().toString());
        properties.put("hibernate.order_updates", databaseConfigPostgre.getOrderUpdates().toString());
        properties.put("hibernate.batch_versioned_data", databaseConfigPostgre.getBatchVersionedData().toString());
        properties.put("hibernate.format_sql", databaseConfigPostgre.getFormatSql().toString());
        properties.put("hibernate.show_sql", databaseConfigPostgre.getShowSql().toString());
        entityManagerFactoryBean.setJpaPropertyMap(properties);

        return entityManagerFactoryBean;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

}
