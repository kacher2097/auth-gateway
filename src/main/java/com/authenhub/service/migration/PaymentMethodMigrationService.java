package com.authenhub.service.migration;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.PaymentMethod;
import com.authenhub.repository.PaymentMethodRepository;
import com.authenhub.repository.jpa.PaymentMethodJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMethodMigrationService implements MigrationService {

    private final PaymentMethodRepository mongoRepository;
    private final PaymentMethodJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseSwitcherConfig;

    @Override
    public void migrateFromMongoToPostgres() {
        log.info("Migrating payment methods from MongoDB to PostgreSQL");
        
        List<com.authenhub.entity.mongo.PaymentMethod> methods = mongoRepository.findAll();
        log.info("Found {} payment methods in MongoDB", methods.size());
        
        List<PaymentMethod> jpaMethods = methods.stream()
                .map(PaymentMethod::fromMongo)
                .toList();
        
        jpaRepository.saveAll(jpaMethods);
        
        log.info("Successfully migrated {} payment methods from MongoDB to PostgreSQL", methods.size());
    }

    @Override
    public void migrateFromPostgresToMongo() {
        log.info("Migrating payment methods from PostgreSQL to MongoDB");
        
        List<PaymentMethod> methods = jpaRepository.findAll();
        log.info("Found {} payment methods in PostgreSQL", methods.size());
        
        List<com.authenhub.entity.mongo.PaymentMethod> mongoMethods = methods.stream()
                .map(PaymentMethod::toMongo)
                .toList();
        
        mongoRepository.saveAll(mongoMethods);
        
        log.info("Successfully migrated {} payment methods from PostgreSQL to MongoDB", methods.size());
    }
}
