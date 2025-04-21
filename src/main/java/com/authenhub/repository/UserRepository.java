package com.authenhub.repository;

import com.authenhub.entity.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
//    User findByProviderAndProviderId(String provider, String providerId);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    long countByRole(User.Role role);

    long countByActive(boolean active);

    List<User> findByCreatedAtBetween(Timestamp start, Timestamp end);

    @Query(value = "{socialProvider: {$exists: true, $ne: null}}")
    long countBySocialLogin();
}