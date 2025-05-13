package com.authenhub.service.impl;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.bean.facebook.comment.FacebookAutoReplyRuleRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentFilterRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentResponse;
import com.authenhub.entity.mongo.FacebookAutoReplyRule;
import com.authenhub.entity.mongo.FacebookComment;
import com.authenhub.entity.mongo.FacebookCommentFilter;
import com.authenhub.entity.mongo.FacebookPage;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.FacebookAutoReplyRuleRepository;
import com.authenhub.repository.FacebookCommentFilterRepository;
import com.authenhub.repository.FacebookCommentRepository;
import com.authenhub.repository.FacebookPageRepository;
import com.authenhub.service.client.FacebookGraphApiWrapper;
import com.authenhub.service.interfaces.IFacebookAuthService;
import com.authenhub.service.interfaces.IFacebookCommentService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookCommentService implements IFacebookCommentService {
    private final FacebookCommentRepository commentRepository;
    private final FacebookAutoReplyRuleRepository autoReplyRuleRepository;
    private final FacebookCommentFilterRepository commentFilterRepository;
    private final FacebookPageRepository pageRepository;
    private final IFacebookAuthService authService;
    private final FacebookGraphApiWrapper graphApiWrapper;
    
    @Override
    public List<FacebookCommentResponse> getComments(String postId, String userId, int limit, int offset) {
        // Kiểm tra xem có comments trong database không
        List<FacebookComment> localComments = commentRepository.findByUserIdAndPostId(userId, postId);
        
        if (!localComments.isEmpty()) {
            return localComments.stream()
                    .map(FacebookCommentResponse::fromEntity)
                    .toList();
        }
        
        // Nếu không có trong database, lấy từ Facebook API
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        JsonNode response = graphApiWrapper.getComments(postId, tokenInfo.getAccessToken(), limit, offset);
        
        List<FacebookCommentResponse> comments = new ArrayList<>();
        
        if (response.has("data") && response.get("data").isArray()) {
            JsonNode data = response.get("data");
            
            for (JsonNode comment : data) {
                comments.add(convertJsonToCommentResponse(comment, postId, userId));
            }
        }
        
        return comments;
    }
    
    @Override
    public FacebookCommentResponse createComment(String postId, FacebookCommentRequest request, String userId) {
        // Lấy token
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        
        // Tạo comment trên Facebook
        JsonNode response = graphApiWrapper.createComment(postId, request.getMessage(), tokenInfo.getAccessToken());
        
        // Lưu comment vào database
        String commentId = response.has("id") ? response.get("id").asText() : null;
        
        if (commentId != null) {
            FacebookComment comment = FacebookComment.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .postId(postId)
                    .commentId(commentId)
                    .message(request.getMessage())
                    .fromId(userId)
                    .fromName("You") // Có thể cập nhật sau khi lấy thông tin người dùng
                    .isHidden(false)
                    .isAutoReplied(false)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            
            FacebookComment savedComment = commentRepository.save(comment);
            return FacebookCommentResponse.fromEntity(savedComment);
        }
        
        return null;
    }
    
    @Override
    public boolean deleteComment(String commentId, String userId) {
        // Kiểm tra xem comment có trong database không
        FacebookComment comment = commentRepository.findByCommentId(commentId).orElse(null);
        
        // Lấy token
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        
        // Xóa comment trên Facebook
        boolean deleted = graphApiWrapper.deleteComment(commentId, tokenInfo.getAccessToken());
        
        if (deleted && comment != null) {
            // Xóa comment khỏi database
            commentRepository.delete(comment);
        }
        
        return deleted;
    }
    
    @Override
    public FacebookAutoReplyRule addAutoReplyRule(FacebookAutoReplyRuleRequest request, String userId) {
        // Kiểm tra xem pageId có hợp lệ không
        if (request.getPageId() != null) {
            pageRepository.findByUserIdAndPageId(userId, request.getPageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + request.getPageId()));
        }
        
        // Tạo quy tắc trả lời tự động
        FacebookAutoReplyRule rule = FacebookAutoReplyRule.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .pageId(request.getPageId())
                .postId(request.getPostId())
                .keyword(request.getKeyword())
                .replyTemplate(request.getReplyTemplate())
                .isActive(request.isActive())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        
        return autoReplyRuleRepository.save(rule);
    }
    
    @Override
    public List<FacebookAutoReplyRule> getAutoReplyRules(String targetId, String userId) {
        // Kiểm tra xem targetId là pageId hay postId
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, targetId).orElse(null);
        
        if (page != null) {
            // Nếu là pageId, lấy quy tắc theo pageId
            return autoReplyRuleRepository.findByUserIdAndPageIdAndIsActiveTrue(userId, targetId);
        } else {
            // Nếu không phải pageId, lấy quy tắc theo postId
            return autoReplyRuleRepository.findByUserIdAndPostIdAndIsActiveTrue(userId, targetId);
        }
    }
    
    @Override
    public boolean deleteAutoReplyRule(String ruleId, String userId) {
        FacebookAutoReplyRule rule = autoReplyRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Auto reply rule not found with id: " + ruleId));
        
        if (!rule.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to delete this rule");
        }
        
        autoReplyRuleRepository.delete(rule);
        return true;
    }
    
    @Override
    public FacebookCommentFilter addCommentFilterRule(FacebookCommentFilterRequest request, String userId) {
        // Kiểm tra xem pageId có hợp lệ không
        if (request.getPageId() != null) {
            pageRepository.findByUserIdAndPageId(userId, request.getPageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + request.getPageId()));
        }
        
        // Tạo quy tắc lọc comment
        FacebookCommentFilter filter = FacebookCommentFilter.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .pageId(request.getPageId())
                .postId(request.getPostId())
                .blockedKeywords(request.getBlockedKeywords())
                .hideMatches(request.isHideMatches())
                .notifyOnMatch(request.isNotifyOnMatch())
                .isActive(request.isActive())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        
        return commentFilterRepository.save(filter);
    }
    
    private FacebookCommentResponse convertJsonToCommentResponse(JsonNode comment, String postId, String userId) {
        String commentId = comment.get("id").asText();
        String message = comment.has("message") ? comment.get("message").asText() : "";
        
        // Extract from information
        String fromId = null;
        String fromName = null;
        if (comment.has("from")) {
            fromId = comment.get("from").get("id").asText();
            fromName = comment.get("from").get("name").asText();
        }
        
        // Extract created time
        Timestamp createdAt = null;
        if (comment.has("created_time")) {
            String createdTime = comment.get("created_time").asText();
            try {
                // Parse ISO 8601 date format
                createdAt = Timestamp.valueOf(createdTime.replace('T', ' ').substring(0, 19));
            } catch (Exception e) {
                log.error("Error parsing created_time: {}", e.getMessage());
                createdAt = new Timestamp(System.currentTimeMillis());
            }
        } else {
            createdAt = new Timestamp(System.currentTimeMillis());
        }
        
        // Save to database
        FacebookComment commentEntity = FacebookComment.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .postId(postId)
                .commentId(commentId)
                .message(message)
                .fromId(fromId)
                .fromName(fromName)
                .isHidden(false)
                .isAutoReplied(false)
                .createdAt(createdAt)
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        
        commentRepository.save(commentEntity);
        
        return FacebookCommentResponse.fromEntity(commentEntity);
    }
}
