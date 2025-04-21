package com.authenhub.repository.adapter;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.PasswordResetToken;
import com.authenhub.repository.PasswordResetTokenRepository;
import com.authenhub.repository.jpa.PasswordResetTokenJpaRepository;
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
 * Adapter cho PasswordResetTokenRepository để chuyển đổi giữa MongoDB và PostgreSQL
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements RepositoryAdapter<com.authenhub.entity.mongo.PasswordResetToken, String> {

    private final PasswordResetTokenRepository mongoRepository;
    private final PasswordResetTokenJpaRepository jpaRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    @Override
    public com.authenhub.entity.mongo.PasswordResetToken save(com.authenhub.entity.mongo.PasswordResetToken token) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.save(token);
        } else {
            PasswordResetToken tokenJpa = PasswordResetToken.fromMongo(token);
            tokenJpa = jpaRepository.save(tokenJpa);
            return tokenJpa.toMongo();
        }
    }

    @Override
    public List<com.authenhub.entity.mongo.PasswordResetToken> saveAll(Iterable<com.authenhub.entity.mongo.PasswordResetToken> tokens) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.saveAll(tokens);
        } else {
            List<PasswordResetToken> tokenJpas = StreamSupport.stream(tokens.spliterator(), false)
                    .map(PasswordResetToken::fromMongo)
                    .collect(Collectors.toList());
            tokenJpas = jpaRepository.saveAll(tokenJpas);
            return tokenJpas.stream()
                    .map(PasswordResetToken::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Optional<com.authenhub.entity.mongo.PasswordResetToken> findById(String id) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findById(id);
        } else {
            try {
                Long longId = Long.parseLong(id);
                return jpaRepository.findById(longId)
                        .map(PasswordResetToken::toMongo);
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
    public List<com.authenhub.entity.mongo.PasswordResetToken> findAll() {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll();
        } else {
            return jpaRepository.findAll().stream()
                    .map(PasswordResetToken::toMongo)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Page<com.authenhub.entity.mongo.PasswordResetToken> findAll(Pageable pageable) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findAll(pageable);
        } else {
            Page<PasswordResetToken> jpaPage = jpaRepository.findAll(pageable);
            List<com.authenhub.entity.mongo.PasswordResetToken> tokens = jpaPage.getContent().stream()
                    .map(PasswordResetToken::toMongo)
                    .collect(Collectors.toList());
            return new PageImpl<>(tokens, pageable, jpaPage.getTotalElements());
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
    public void delete(com.authenhub.entity.mongo.PasswordResetToken token) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.delete(token);
        } else {
            try {
                Long longId = Long.parseLong(token.getId());
                jpaRepository.deleteById(longId);
            } catch (NumberFormatException e) {
                log.warn("Invalid ID format for PostgreSQL: {}", token.getId());
            }
        }
    }

    @Override
    public void deleteAll(Iterable<com.authenhub.entity.mongo.PasswordResetToken> tokens) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteAll(tokens);
        } else {
            List<Long> ids = StreamSupport.stream(tokens.spliterator(), false)
                    .map(com.authenhub.entity.mongo.PasswordResetToken::getId)
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
            
            List<PasswordResetToken> tokenJpas = jpaRepository.findAllById(ids);
            jpaRepository.deleteAll(tokenJpas);
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

    // Các phương thức đặc biệt của PasswordResetTokenRepository

    public Optional<com.authenhub.entity.mongo.PasswordResetToken> findByToken(String token) {
        if (databaseConfig.isMongoActive()) {
            return mongoRepository.findByToken(token);
        } else {
            return jpaRepository.findByToken(token)
                    .map(PasswordResetToken::toMongo);
        }
    }

    public void deleteByUserId(String userId) {
        if (databaseConfig.isMongoActive()) {
            mongoRepository.deleteByUserId(userId);
        } else {
            jpaRepository.deleteByUserId(userId);
        }
    }
}
