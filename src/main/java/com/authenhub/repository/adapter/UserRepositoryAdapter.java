package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.User;
import com.authenhub.repository.UserRepository;
import com.authenhub.repository.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter cho UserRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.User, String> {

    private final UserRepository mongoRepository;
    private final UserJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.User save(com.authenhub.entity.mongo.User user) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(user);
        } else {
            User userJpa = User.fromMongo(user);
            userJpa = jpaRepository.save(userJpa);
            return userJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.User> saveAll(Iterable<com.authenhub.entity.mongo.User> users) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(users);
        } else {
            List<User> userJpas = StreamSupport.stream(users.spliterator(), false)
                    .map(User::fromMongo)
                    .collect(Collectors.toList());
            userJpas = jpaRepository.saveAll(userJpas);
            return userJpas.stream()
                    .map(User::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.User> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(User::toMongo);
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
    public List<com.authenhub.entity.mongo.User> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(User::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.User> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<User> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.User> users = jpaPage.getContent().stream()
                    .map(User::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(users, pageable, jpaPage.getTotalElements());
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
    public void delete(com.authenhub.entity.mongo.User user) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(user);
        } else {
            try {
                Long longId = Long.parseLong(user.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", user.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.User> users) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(users);
        } else {
            List<Long> ids = StreamSupport.stream(users.spliterator(), false)
                    .map(com.authenhub.entity.mongo.User::getId)
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
            
            List<User> userJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(userJpas);
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

    // Các phương thức đặc biệt của UserRepository

    public Optional<com.authenhub.entity.mongo.User> findByUsername(String username) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByUsername(username);
        } else {
            return jpaRepository.findByUsername(username)
                    .map(User::toMongo);
        }
    }

    public Optional<com.authenhub.entity.mongo.User> findByEmail(String email) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByEmail(email);
        } else {
            return jpaRepository.findByEmail(email)
                    .map(User::toMongo);
        }
    }

    public Boolean existsByUsername(String username) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.existsByUsername(username);
        } else {
            return jpaRepository.existsByUsername(username);
        }
    }

    public Boolean existsByEmail(String email) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.existsByEmail(email);
        } else {
            return jpaRepository.existsByEmail(email);
        }
    }

    public long countByRole(com.authenhub.entity.mongo.User.Role role) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countByRole(role);
        } else {
            return jpaRepository.countByRole(role);
        }
    }

    public long countByActive(boolean active) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countByActive(active);
        } else {
            return jpaRepository.countByActive(active);
        }
    }

    public List<com.authenhub.entity.mongo.User> findByCreatedAtBetween(Timestamp start, Timestamp end) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByCreatedAtBetween(start, end);
        } else {
            return jpaRepository.findByCreatedAtBetween(start, end).stream()
                    .map(User::toMongo)
                    .collect(Collectors.toList());
        }
    }

    public long countBySocialLogin() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.countBySocialLogin();
        } else {
            return jpaRepository.countBySocialLogin();
        }
    }
}
