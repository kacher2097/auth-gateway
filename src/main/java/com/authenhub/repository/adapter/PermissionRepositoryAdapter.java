package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.Permission;
import com.authenhub.repository.PermissionRepository;
import com.authenhub.repository.jpa.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter cho PermissionRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.Permission, String> {

    private final PermissionRepository mongoRepository;
    private final PermissionJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.Permission save(com.authenhub.entity.mongo.Permission permission) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(permission);
        } else {
            Permission permissionJpa = Permission.fromMongo(permission);
            permissionJpa = jpaRepository.save(permissionJpa);
            return permissionJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.Permission> saveAll(Iterable<com.authenhub.entity.mongo.Permission> permissions) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(permissions);
        } else {
            List<Permission> permissionJpas = StreamSupport.stream(permissions.spliterator(), false)
                    .map(Permission::fromMongo)
                    .collect(Collectors.toList());
            permissionJpas = jpaRepository.saveAll(permissionJpas);
            return permissionJpas.stream()
                    .map(Permission::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.Permission> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(Permission::toMongo);
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
    public List<com.authenhub.entity.mongo.Permission> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(Permission::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.Permission> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<Permission> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.Permission> permissions = jpaPage.getContent().stream()
                    .map(Permission::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(permissions, pageable, jpaPage.getTotalElements());
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
    public void delete(com.authenhub.entity.mongo.Permission permission) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(permission);
        } else {
            try {
                Long longId = Long.parseLong(permission.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", permission.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.Permission> permissions) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(permissions);
        } else {
            List<Long> ids = StreamSupport.stream(permissions.spliterator(), false)
                    .map(com.authenhub.entity.mongo.Permission::getId)
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
            
            List<Permission> permissionJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(permissionJpas);
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

    // Các phương thức đặc biệt của PermissionRepository

    public Optional<com.authenhub.entity.mongo.Permission> findByName(String name) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByName(name);
        } else {
            return jpaRepository.findByName(name)
                    .map(Permission::toMongo);
        }
    }

    public List<com.authenhub.entity.mongo.Permission> findByCategory(String category) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByCategory(category);
        } else {
            return jpaRepository.findByCategory(category).stream()
                    .map(Permission::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public List<com.authenhub.entity.mongo.Permission> findAllById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAllById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                Optional<Permission> permissionJpa = jpaRepository.findById(longId);
                List<com.authenhub.entity.mongo.Permission> result = new ArrayList<>();
                permissionJpa.ifPresent(jpa -> result.add(jpa.toMongo()));
                return result;
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", id);
                return new ArrayList<>();
            }
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
