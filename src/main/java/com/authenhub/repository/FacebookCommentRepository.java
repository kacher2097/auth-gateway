package com.authenhub.repository;

import com.authenhub.entity.mongo.FacebookComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacebookCommentRepository extends MongoRepository<FacebookComment, String> {
    List<FacebookComment> findByPostId(String postId);
    Page<FacebookComment> findByPostId(String postId, Pageable pageable);
    Optional<FacebookComment> findByCommentId(String commentId);
    List<FacebookComment> findByUserIdAndPostId(String userId, String postId);
}
