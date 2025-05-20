package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.common.SimpleApiResponse;
import com.authenhub.bean.facebook.comment.FacebookAutoReplyRuleRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentFilterRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentRequest;
import com.authenhub.bean.facebook.comment.FacebookCommentResponse;
import com.authenhub.entity.mongo.FacebookAutoReplyRule;
import com.authenhub.entity.mongo.FacebookCommentFilter;
import com.authenhub.service.interfaces.IFacebookCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facebook/comments")
@RequiredArgsConstructor
@Tag(name = "Facebook Comments", description = "API để quản lý bình luận Facebook")
public class FacebookCommentController {

    private final IFacebookCommentService commentService;

    @GetMapping
    @Operation(summary = "Lấy danh sách bình luận",
            description = "Lấy danh sách bình luận của một bài đăng.")
    public ApiResponse<?> getComments(
            @RequestParam String postId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<FacebookCommentResponse> comments = commentService.getComments(postId, userId, limit, offset);
        return ApiResponse.success(comments);
    }

    @PostMapping
    @Operation(summary = "Tạo bình luận mới",
            description = "Tạo một bình luận mới trên bài đăng.")
    public ApiResponse<?> createComment(
            @RequestParam String postId,
            @RequestParam String userId,
            @RequestBody FacebookCommentRequest request) {
        FacebookCommentResponse comment = commentService.createComment(postId, request, userId);
        return ApiResponse.success(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Xóa bình luận",
            description = "Xóa một bình luận dựa trên ID.")
    public ApiResponse<?> deleteComment(
            @PathVariable String commentId,
            @RequestParam String userId) {
        boolean deleted = commentService.deleteComment(commentId, userId);

        SimpleApiResponse response = SimpleApiResponse.builder()
                .success(deleted)
                .message(deleted ? "Comment deleted successfully" : "Failed to delete comment")
                .build();

        return ApiResponse.success(response);
    }

    @PostMapping("/auto-reply")
    @Operation(summary = "Thêm quy tắc trả lời tự động",
            description = "Thêm một quy tắc trả lời tự động cho bình luận.")
    public ApiResponse<?> addAutoReplyRule(
            @RequestParam String userId,
            @RequestBody FacebookAutoReplyRuleRequest request) {
        FacebookAutoReplyRule rule = commentService.addAutoReplyRule(request, userId);
        return ApiResponse.success(rule);
    }

    @GetMapping("/auto-reply")
    @Operation(summary = "Lấy danh sách quy tắc trả lời tự động",
            description = "Lấy danh sách các quy tắc trả lời tự động cho bình luận.")
    public ApiResponse<?> getAutoReplyRules(
            @RequestParam String targetId,
            @RequestParam String userId) {
        List<FacebookAutoReplyRule> rules = commentService.getAutoReplyRules(targetId, userId);
        return ApiResponse.success(rules);
    }

    @DeleteMapping("/auto-reply/{ruleId}")
    @Operation(summary = "Xóa quy tắc trả lời tự động",
            description = "Xóa một quy tắc trả lời tự động dựa trên ID.")
    public ApiResponse<?> deleteAutoReplyRule(
            @PathVariable String ruleId,
            @RequestParam String userId) {
        boolean deleted = commentService.deleteAutoReplyRule(ruleId, userId);

        SimpleApiResponse response = SimpleApiResponse.builder()
                .success(deleted)
                .message(deleted ? "Auto reply rule deleted successfully" : "Failed to delete auto reply rule")
                .build();

        return ApiResponse.success(response);
    }

    @PostMapping("/filter")
    @Operation(summary = "Thêm quy tắc lọc bình luận",
            description = "Thêm một quy tắc lọc bình luận để tự động xử lý bình luận không mong muốn.")
    public ApiResponse<?> addCommentFilterRule(
            @RequestParam String userId,
            @RequestBody FacebookCommentFilterRequest request) {
        FacebookCommentFilter filter = commentService.addCommentFilterRule(request, userId);
        return ApiResponse.success(filter);
    }
}
