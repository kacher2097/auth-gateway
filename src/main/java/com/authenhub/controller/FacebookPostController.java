package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.facebook.post.FacebookPostListResponse;
import com.authenhub.bean.facebook.post.FacebookPostRequest;
import com.authenhub.bean.facebook.post.FacebookPostResponse;
import com.authenhub.bean.facebook.post.FacebookScheduledPostRequest;
import com.authenhub.service.interfaces.IFacebookPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facebook/posts")
@RequiredArgsConstructor
@Tag(name = "Facebook Posts", description = "API để quản lý bài đăng Facebook")
public class FacebookPostController {

    private final IFacebookPostService postService;

    @GetMapping
    @Operation(summary = "Lấy danh sách bài đăng",
            description = "Lấy danh sách bài đăng từ một trang hoặc người dùng Facebook.")
    public ApiResponse<?> getPosts(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        FacebookPostListResponse posts = postService.getPosts(targetId, userId, limit, offset);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Lấy chi tiết bài đăng",
            description = "Lấy thông tin chi tiết của một bài đăng dựa trên ID.")
    public ApiResponse<?> getPostDetails(
            @PathVariable String postId,
            @RequestParam String userId) {
        FacebookPostResponse post = postService.getPostDetails(postId, userId);
        return ApiResponse.success(post);
    }

    @PostMapping
    @Operation(summary = "Tạo bài đăng mới",
            description = "Tạo một bài đăng mới trên Facebook.")
    public ApiResponse<?> createPost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.createPost(targetId, request, userId);
        return ApiResponse.success(post);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Cập nhật bài đăng",
            description = "Cập nhật nội dung của một bài đăng dựa trên ID.")
    public ApiResponse<?> updatePost(
            @PathVariable String postId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.updatePost(postId, request, userId);
        return ApiResponse.success(post);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Xóa bài đăng",
            description = "Xóa một bài đăng dựa trên ID.")
    public ApiResponse<?> deletePost(
            @PathVariable String postId,
            @RequestParam String userId) {
        boolean deleted = postService.deletePost(postId, userId);

        if (deleted) {
            return ApiResponse.success("Post deleted successfully");
        } else {
            return ApiResponse.error("99", "Failed to delete post");
        }
    }

    @PostMapping("/schedule")
    @Operation(summary = "Lên lịch đăng bài",
            description = "Lên lịch đăng một bài viết vào thời điểm cụ thể trong tương lai.")
    public ApiResponse<?> schedulePost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookScheduledPostRequest request) {
        FacebookPostResponse post = postService.schedulePost(targetId, request, userId);
        return ApiResponse.success(post);
    }
}
