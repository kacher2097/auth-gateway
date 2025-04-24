package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FacebookTokenRepository extends MongoRepository<FacebookToken, String> {
    Optional<FacebookToken> findByUserId(String userId);
    void deleteByUserId(String userId);
}
