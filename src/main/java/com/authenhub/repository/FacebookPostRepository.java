package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacebookPostRepository extends MongoRepository<FacebookPost, String> {
    List<FacebookPost> findByUserId(String userId);
    List<FacebookPost> findByUserIdAndPageId(String userId, String pageId);
    Optional<FacebookPost> findByUserIdAndPostId(String userId, String postId);
    Page<FacebookPost> findByUserIdAndPageId(String userId, String pageId, Pageable pageable);
    List<FacebookPost> findByScheduledPublishTimeBeforeAndIsPublishedFalse(Timestamp timestamp);
}
