package com.authenhub.service.impl;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.bean.facebook.post.FacebookPostListResponse;
import com.authenhub.bean.facebook.post.FacebookPostRequest;
import com.authenhub.bean.facebook.post.FacebookPostResponse;
import com.authenhub.bean.facebook.post.FacebookScheduledPostRequest;
import com.authenhub.entity.mongo.FacebookPage;
import com.authenhub.entity.mongo.FacebookPost;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.FacebookPageRepository;
import com.authenhub.repository.FacebookPostRepository;
import com.authenhub.service.client.FacebookGraphApiWrapper;
import com.authenhub.service.interfaces.IFacebookAuthService;
import com.authenhub.service.interfaces.IFacebookPostService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookPostService implements IFacebookPostService {
    private final FacebookPostRepository postRepository;
    private final FacebookPageRepository pageRepository;
    private final IFacebookAuthService authService;
    private final FacebookGraphApiWrapper graphApiWrapper;
    
    @Override
    public FacebookPostListResponse getPosts(String targetId, String userId, int limit, int offset) {
        // Kiểm tra xem targetId có phải là pageId không
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, targetId).orElse(null);
        
        if (page != null) {
            // Nếu là pageId, lấy bài viết từ database
            Pageable pageable = PageRequest.of(offset / limit, limit);
            Page<FacebookPost> postsPage = postRepository.findByUserIdAndPageId(userId, targetId, pageable);
            
            List<FacebookPostResponse> posts = postsPage.getContent().stream()
                    .map(FacebookPostResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return FacebookPostListResponse.builder()
                    .posts(posts)
                    .totalCount((int) postsPage.getTotalElements())
                    .page(postsPage.getNumber())
                    .size(postsPage.getSize())
                    .hasNext(postsPage.hasNext())
                    .build();
        } else {
            // Nếu không phải pageId, lấy bài viết từ Facebook API
            FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
            JsonNode response = graphApiWrapper.getPosts(targetId, tokenInfo.getAccessToken(), limit, offset);
            
            List<FacebookPostResponse> posts = new ArrayList<>();
            int totalCount = 0;
            
            if (response.has("data") && response.get("data").isArray()) {
                JsonNode data = response.get("data");
                totalCount = data.size();
                
                for (JsonNode post : data) {
                    posts.add(convertJsonToPostResponse(post, userId, null));
                }
            }
            
            boolean hasNext = response.has("paging") && response.get("paging").has("next");
            
            return FacebookPostListResponse.builder()
                    .posts(posts)
                    .totalCount(totalCount)
                    .page(offset / limit)
                    .size(limit)
                    .hasNext(hasNext)
                    .build();
        }
    }
    
    @Override
    public FacebookPostResponse getPostDetails(String postId, String userId) {
        // Kiểm tra xem bài viết có trong database không
        return postRepository.findByUserIdAndPostId(userId, postId)
                .map(FacebookPostResponse::fromEntity)
                .orElseGet(() -> {
                    // Nếu không có trong database, lấy từ Facebook API
                    FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
                    JsonNode post = graphApiWrapper.getPostDetails(postId, tokenInfo.getAccessToken());
                    return convertJsonToPostResponse(post, userId, null);
                });
    }
    
    @Override
    public FacebookPostResponse createPost(String targetId, FacebookPostRequest request, String userId) {
        // Kiểm tra xem targetId có phải là pageId không
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, targetId).orElse(null);
        String accessToken;
        
        if (page != null) {
            // Nếu là pageId, sử dụng page token
            accessToken = page.getPageToken();
        } else {
            // Nếu không phải pageId, sử dụng user token
            accessToken = authService.getUserToken(userId).getAccessToken();
        }
        
        // Tạo bài viết trên Facebook
        JsonNode response = graphApiWrapper.createPost(
                targetId, 
                request.getMessage(), 
                request.getLink(), 
                accessToken
        );
        
        // Lưu bài viết vào database
        String postId = response.has("id") ? response.get("id").asText() : null;
        
        if (postId != null) {
            FacebookPost post = FacebookPost.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .pageId(page != null ? page.getPageId() : null)
                    .postId(postId)
                    .message(request.getMessage())
                    .link(request.getLink())
                    .picture(request.getPicture())
                    .type(request.getType())
                    .isPublished(true)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .publishedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            
            FacebookPost savedPost = postRepository.save(post);
            return FacebookPostResponse.fromEntity(savedPost);
        }
        
        return null;
    }
    
    @Override
    public FacebookPostResponse updatePost(String postId, FacebookPostRequest request, String userId) {
        // Kiểm tra xem bài viết có trong database không
        FacebookPost post = postRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Lấy token
        String accessToken;
        if (post.getPageId() != null) {
            // Nếu là bài viết của trang, sử dụng page token
            FacebookPage page = pageRepository.findByUserIdAndPageId(userId, post.getPageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + post.getPageId()));
            accessToken = page.getPageToken();
        } else {
            // Nếu là bài viết của người dùng, sử dụng user token
            accessToken = authService.getUserToken(userId).getAccessToken();
        }
        
        // Cập nhật bài viết trên Facebook
        graphApiWrapper.updatePost(postId, request.getMessage(), accessToken);
        
        // Cập nhật bài viết trong database
        post.setMessage(request.getMessage());
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        FacebookPost updatedPost = postRepository.save(post);
        return FacebookPostResponse.fromEntity(updatedPost);
    }
    
    @Override
    public boolean deletePost(String postId, String userId) {
        // Kiểm tra xem bài viết có trong database không
        FacebookPost post = postRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        
        // Lấy token
        String accessToken;
        if (post.getPageId() != null) {
            // Nếu là bài viết của trang, sử dụng page token
            FacebookPage page = pageRepository.findByUserIdAndPageId(userId, post.getPageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + post.getPageId()));
            accessToken = page.getPageToken();
        } else {
            // Nếu là bài viết của người dùng, sử dụng user token
            accessToken = authService.getUserToken(userId).getAccessToken();
        }
        
        // Xóa bài viết trên Facebook
        boolean deleted = graphApiWrapper.deletePost(postId, accessToken);
        
        if (deleted) {
            // Xóa bài viết khỏi database
            postRepository.delete(post);
        }
        
        return deleted;
    }
    
    @Override
    public FacebookPostResponse schedulePost(String targetId, FacebookScheduledPostRequest request, String userId) {
        // Kiểm tra xem targetId có phải là pageId không
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, targetId).orElse(null);
        String accessToken;
        
        if (page != null) {
            // Nếu là pageId, sử dụng page token
            accessToken = page.getPageToken();
        } else {
            // Nếu không phải pageId, sử dụng user token
            accessToken = authService.getUserToken(userId).getAccessToken();
        }
        
        // Lên lịch đăng bài trên Facebook
        long publishTime = request.getScheduledPublishTime().getTime() / 1000; // Convert to seconds
        JsonNode response = graphApiWrapper.schedulePost(
                targetId, 
                request.getMessage(), 
                request.getLink(), 
                publishTime,
                accessToken
        );
        
        // Lưu bài viết vào database
        String postId = response.has("id") ? response.get("id").asText() : null;
        
        if (postId != null) {
            FacebookPost post = FacebookPost.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .pageId(page != null ? page.getPageId() : null)
                    .postId(postId)
                    .message(request.getMessage())
                    .link(request.getLink())
                    .picture(request.getPicture())
                    .type(request.getType())
                    .scheduledPublishTime(request.getScheduledPublishTime())
                    .isPublished(false)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            
            FacebookPost savedPost = postRepository.save(post);
            return FacebookPostResponse.fromEntity(savedPost);
        }
        
        return null;
    }
    
    private FacebookPostResponse convertJsonToPostResponse(JsonNode post, String userId, String pageId) {
        String postId = post.get("id").asText();
        String message = post.has("message") ? post.get("message").asText() : "";
        
        // Extract likes count
        int likesCount = 0;
        if (post.has("likes") && post.get("likes").has("summary") && post.get("likes").get("summary").has("total_count")) {
            likesCount = post.get("likes").get("summary").get("total_count").asInt();
        }
        
        // Extract comments count
        int commentsCount = 0;
        if (post.has("comments") && post.get("comments").has("summary") && post.get("comments").get("summary").has("total_count")) {
            commentsCount = post.get("comments").get("summary").get("total_count").asInt();
        }
        
        // Extract shares count
        int sharesCount = 0;
        if (post.has("shares") && post.get("shares").has("count")) {
            sharesCount = post.get("shares").get("count").asInt();
        }
        
        // Extract link and picture
        String link = post.has("permalink_url") ? post.get("permalink_url").asText() : null;
        String picture = null;
        if (post.has("attachments") && post.get("attachments").has("data") && post.get("attachments").get("data").isArray() 
                && post.get("attachments").get("data").size() > 0) {
            JsonNode attachment = post.get("attachments").get("data").get(0);
            if (attachment.has("media") && attachment.get("media").has("image") && attachment.get("media").get("image").has("src")) {
                picture = attachment.get("media").get("image").get("src").asText();
            }
        }
        
        // Extract created time
        Timestamp createdAt = null;
        if (post.has("created_time")) {
            String createdTime = post.get("created_time").asText();
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
        
        return FacebookPostResponse.builder()
                .postId(postId)
                .pageId(pageId)
                .message(message)
                .link(link)
                .picture(picture)
                .type(determinePostType(post))
                .isPublished(true)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .publishedAt(createdAt)
                .likesCount(likesCount)
                .commentsCount(commentsCount)
                .sharesCount(sharesCount)
                .build();
    }
    
    private String determinePostType(JsonNode post) {
        if (post.has("attachments") && post.get("attachments").has("data") && post.get("attachments").get("data").isArray() 
                && post.get("attachments").get("data").size() > 0) {
            JsonNode attachment = post.get("attachments").get("data").get(0);
            if (attachment.has("type")) {
                String type = attachment.get("type").asText();
                switch (type) {
                    case "photo":
                        return "photo";
                    case "video":
                        return "video";
                    case "share":
                    case "link":
                        return "link";
                    default:
                        return "status";
                }
            }
        }
        return "status";
    }
}
