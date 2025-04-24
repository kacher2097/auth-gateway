package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookPage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacebookPageRepository extends MongoRepository<FacebookPage, String> {
    List<FacebookPage> findByUserId(String userId);
    Optional<FacebookPage> findByUserIdAndPageId(String userId, String pageId);
    void deleteByUserIdAndPageId(String userId, String pageId);
}
