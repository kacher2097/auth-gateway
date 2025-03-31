package com.authenhub.repository;

import com.authenhub.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
//    User findByProviderAndProviderId(String provider, String providerId);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
} 