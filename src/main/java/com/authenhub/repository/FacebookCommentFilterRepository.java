package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookCommentFilter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacebookCommentFilterRepository extends MongoRepository<FacebookCommentFilter, String> {
    List<FacebookCommentFilter> findByUserIdAndIsActiveTrue(String userId);
    List<FacebookCommentFilter> findByUserIdAndPageIdAndIsActiveTrue(String userId, String pageId);
    Optional<FacebookCommentFilter> findByUserIdAndPostIdAndIsActiveTrue(String userId, String postId);
}
