package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.PaymentMethod;
import com.authenhub.repository.PaymentMethodRepository;
import com.authenhub.repository.jpa.PaymentMethodJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter cho PaymentMethodRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMethodRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.PaymentMethod, String> {

    private final PaymentMethodRepository mongoRepository;
    private final PaymentMethodJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.PaymentMethod save(com.authenhub.entity.mongo.PaymentMethod paymentMethod) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(paymentMethod);
        } else {
            PaymentMethod paymentMethodJpa = PaymentMethod.fromMongo(paymentMethod);
            paymentMethodJpa = jpaRepository.save(paymentMethodJpa);
            return paymentMethodJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.PaymentMethod> saveAll(Iterable<com.authenhub.entity.mongo.PaymentMethod> paymentMethods) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(paymentMethods);
        } else {
            List<PaymentMethod> paymentMethodJpas = StreamSupport.stream(paymentMethods.spliterator(), false)
                    .map(PaymentMethod::fromMongo)
                    .collect(Collectors.toList());
            paymentMethodJpas = jpaRepository.saveAll(paymentMethodJpas);
            return paymentMethodJpas.stream()
                    .map(PaymentMethod::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.PaymentMethod> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(PaymentMethod::toMongo);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
                return Optional.empty();
            }
        }
    }

    @Override
    public boolean existsById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.existsById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.existsById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
                return false;
            }
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.PaymentMethod> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(PaymentMethod::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.PaymentMethod> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<PaymentMethod> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.PaymentMethod> paymentMethods = jpaPage.getContent().stream()
                    .map(PaymentMethod::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(paymentMethods, pageable, jpaPage.getTotalElements());
        }
    }

    @Override
    public long count() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.count();
        } else {
            return jpaRepository.count();
        }
    }

    @Override
    public void deleteById(String id) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
            }
        }
    }

    @Override
    public void delete(com.authenhub.entity.mongo.PaymentMethod paymentMethod) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(paymentMethod);
        } else {
            try {
                Long longId = Long.parseLong(paymentMethod.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", paymentMethod.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.PaymentMethod> paymentMethods) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(paymentMethods);
        } else {
            List<Long> ids = StreamSupport.stream(paymentMethods.spliterator(), false)
                    .map(com.authenhub.entity.mongo.PaymentMethod::getId)
                    .map(id -> {
                        try {
                            return Long.parseLong(id);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid ID format for PostgreSQL: {}", id);
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            List<PaymentMethod> paymentMethodJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(paymentMethodJpas);
        }
    }

    @Override
    public void deleteAll() {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll();
        } else {
            jpaRepository.deleteAll();
        }
    }

    // Các phương thức đặc biệt của PaymentMethodRepository

    public List<com.authenhub.entity.mongo.PaymentMethod> findByIsActiveTrue() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByIsActiveTrue();
        } else {
            return jpaRepository.findByIsActiveTrue().stream()
                    .map(PaymentMethod::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public Optional<com.authenhub.entity.mongo.PaymentMethod> findByName(String name) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByName(name);
        } else {
            return jpaRepository.findByName(name)
                    .map(PaymentMethod::toMongo);
        }
    }

    public List<com.authenhub.entity.mongo.PaymentMethod> findByProviderType(String providerType) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByProviderType(providerType);
        } else {
            return jpaRepository.findByProviderType(providerType).stream()
                    .map(PaymentMethod::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public boolean existsByName(String name) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.existsByName(name);
        } else {
            return jpaRepository.existsByName(name);
        }
    }
}
