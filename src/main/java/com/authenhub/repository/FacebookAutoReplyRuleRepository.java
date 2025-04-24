package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookAutoReplyRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacebookAutoReplyRuleRepository extends MongoRepository<FacebookAutoReplyRule, String> {
    List<FacebookAutoReplyRule> findByUserIdAndIsActiveTrue(String userId);
    List<FacebookAutoReplyRule> findByUserIdAndPageIdAndIsActiveTrue(String userId, String pageId);
    List<FacebookAutoReplyRule> findByUserIdAndPostIdAndIsActiveTrue(String userId, String postId);
}
