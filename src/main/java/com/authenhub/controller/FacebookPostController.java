package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.facebook.post.FacebookPostListResponse;
import com.authenhub.bean.facebook.post.FacebookPostRequest;
import com.authenhub.bean.facebook.post.FacebookPostResponse;
import com.authenhub.bean.facebook.post.FacebookScheduledPostRequest;
import com.authenhub.service.interfaces.IFacebookPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facebook/posts")
@RequiredArgsConstructor
public class FacebookPostController {

    private final IFacebookPostService postService;

    @GetMapping
    public ApiResponse<?> getPosts(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        FacebookPostListResponse posts = postService.getPosts(targetId, userId, limit, offset);
        return ApiResponse.success(posts);
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getPostDetails(
            @PathVariable String postId,
            @RequestParam String userId) {
        FacebookPostResponse post = postService.getPostDetails(postId, userId);
        return ApiResponse.success(post);
    }

    @PostMapping
    public ApiResponse<?> createPost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.createPost(targetId, request, userId);
        return ApiResponse.success(post);
    }

    @PutMapping("/{postId}")
    public ApiResponse<?> updatePost(
            @PathVariable String postId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.updatePost(postId, request, userId);
        return ApiResponse.success(post);
    }

    @DeleteMapping("/{postId}")
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
    public ApiResponse<?> schedulePost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookScheduledPostRequest request) {
        FacebookPostResponse post = postService.schedulePost(targetId, request, userId);
        return ApiResponse.success(post);
    }
}
