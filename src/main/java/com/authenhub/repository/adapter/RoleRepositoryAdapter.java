package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.Role;
import com.authenhub.repository.RoleRepository;
import com.authenhub.repository.jpa.RoleJpaRepository;
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
 * Adapter cho RoleRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.Role, String> {

    private final RoleRepository mongoRepository;
    private final RoleJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.Role save(com.authenhub.entity.mongo.Role role) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(role);
        } else {
            Role roleJpa = Role.fromMongo(role);
            roleJpa = jpaRepository.save(roleJpa);
            return roleJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.Role> saveAll(Iterable<com.authenhub.entity.mongo.Role> roles) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(roles);
        } else {
            List<Role> roleJpas = StreamSupport.stream(roles.spliterator(), false)
                    .map(Role::fromMongo)
                    .collect(Collectors.toList());
            roleJpas = jpaRepository.saveAll(roleJpas);
            return roleJpas.stream()
                    .map(Role::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.Role> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(Role::toMongo);
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
    public List<com.authenhub.entity.mongo.Role> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(Role::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.Role> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<Role> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.Role> roles = jpaPage.getContent().stream()
                    .map(Role::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(roles, pageable, jpaPage.getTotalElements());
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
    public void delete(com.authenhub.entity.mongo.Role role) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(role);
        } else {
            try {
                Long longId = Long.parseLong(role.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", role.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.Role> roles) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(roles);
        } else {
            List<Long> ids = StreamSupport.stream(roles.spliterator(), false)
                    .map(com.authenhub.entity.mongo.Role::getId)
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
            
            List<Role> roleJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(roleJpas);
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

    // Các phương thức đặc biệt của RoleRepository

    public Optional<com.authenhub.entity.mongo.Role> findByName(String name) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByName(name);
        } else {
            return jpaRepository.findByName(name)
                    .map(Role::toMongo);
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
