package com.authenhub.controller;

import com.authenhub.bean.common.SimpleApiResponse;
import com.authenhub.bean.facebook.post.FacebookPostListResponse;
import com.authenhub.bean.facebook.post.FacebookPostRequest;
import com.authenhub.bean.facebook.post.FacebookPostResponse;
import com.authenhub.bean.facebook.post.FacebookScheduledPostRequest;
import com.authenhub.service.interfaces.IFacebookPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facebook/posts")
@RequiredArgsConstructor
public class FacebookPostController {
    
    private final IFacebookPostService postService;
    
    @GetMapping
    public ResponseEntity<FacebookPostListResponse> getPosts(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        FacebookPostListResponse posts = postService.getPosts(targetId, userId, limit, offset);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<FacebookPostResponse> getPostDetails(
            @PathVariable String postId,
            @RequestParam String userId) {
        FacebookPostResponse post = postService.getPostDetails(postId, userId);
        return ResponseEntity.ok(post);
    }
    
    @PostMapping
    public ResponseEntity<FacebookPostResponse> createPost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.createPost(targetId, request, userId);
        return ResponseEntity.ok(post);
    }
    
    @PutMapping("/{postId}")
    public ResponseEntity<FacebookPostResponse> updatePost(
            @PathVariable String postId,
            @RequestParam String userId,
            @RequestBody FacebookPostRequest request) {
        FacebookPostResponse post = postService.updatePost(postId, request, userId);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{postId}")
    public ResponseEntity<SimpleApiResponse> deletePost(
            @PathVariable String postId,
            @RequestParam String userId) {
        boolean deleted = postService.deletePost(postId, userId);
        
        SimpleApiResponse response = SimpleApiResponse.builder()
                .success(deleted)
                .message(deleted ? "Post deleted successfully" : "Failed to delete post")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/schedule")
    public ResponseEntity<FacebookPostResponse> schedulePost(
            @RequestParam String targetId,
            @RequestParam String userId,
            @RequestBody FacebookScheduledPostRequest request) {
        FacebookPostResponse post = postService.schedulePost(targetId, request, userId);
        return ResponseEntity.ok(post);
    }
}
