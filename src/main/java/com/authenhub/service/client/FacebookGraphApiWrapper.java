package com.authenhub.service.client;

import com.authenhub.bean.facebook.auth.FacebookUserResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class for Facebook Graph API to simplify common operations
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FacebookGraphApiWrapper {
    private final FacebookApiClient apiClient;
    
    /**
     * Get user profile information
     * @param accessToken User access token
     * @return User profile information
     */
    public FacebookUserResponse getUserProfile(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,name,email");
        
        return apiClient.get("/me", accessToken, FacebookUserResponse.class, params);
    }
    
    /**
     * Get list of pages managed by user
     * @param accessToken User access token
     * @return List of pages
     */
    public List<JsonNode> getUserPages(String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,name,category,picture,access_token");
        
        JsonNode response = apiClient.get("/me/accounts", accessToken, JsonNode.class, params);
        List<JsonNode> pages = new ArrayList<>();
        
        if (response.has("data") && response.get("data").isArray()) {
            response.get("data").forEach(pages::add);
        }
        
        return pages;
    }
    
    /**
     * Get page details
     * @param pageId Page ID
     * @param accessToken Access token
     * @return Page details
     */
    public JsonNode getPageDetails(String pageId, String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,name,category,picture,fan_count,description");
        
        return apiClient.get("/" + pageId, accessToken, JsonNode.class, params);
    }
    
    /**
     * Get posts from a page or user
     * @param targetId Page or user ID
     * @param accessToken Access token
     * @param limit Number of posts to retrieve
     * @param offset Offset for pagination
     * @return List of posts
     */
    public JsonNode getPosts(String targetId, String accessToken, int limit, int offset) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,message,created_time,permalink_url,attachments,likes.summary(true),comments.summary(true),shares");
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        
        return apiClient.get("/" + targetId + "/posts", accessToken, JsonNode.class, params);
    }
    
    /**
     * Get post details
     * @param postId Post ID
     * @param accessToken Access token
     * @return Post details
     */
    public JsonNode getPostDetails(String postId, String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,message,created_time,permalink_url,attachments,likes.summary(true),comments.summary(true),shares");
        
        return apiClient.get("/" + postId, accessToken, JsonNode.class, params);
    }
    
    /**
     * Create a new post
     * @param targetId Page or user ID
     * @param message Post message
     * @param link Link to include in post
     * @param accessToken Access token
     * @return Created post
     */
    public JsonNode createPost(String targetId, String message, String link, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        
        if (link != null && !link.isEmpty()) {
            body.put("link", link);
        }
        
        return apiClient.post("/" + targetId + "/feed", accessToken, body, JsonNode.class);
    }
    
    /**
     * Schedule a post
     * @param targetId Page or user ID
     * @param message Post message
     * @param link Link to include in post
     * @param publishTime Unix timestamp for scheduled publishing
     * @param accessToken Access token
     * @return Scheduled post
     */
    public JsonNode schedulePost(String targetId, String message, String link, long publishTime, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("published", false);
        body.put("scheduled_publish_time", publishTime);
        
        if (link != null && !link.isEmpty()) {
            body.put("link", link);
        }
        
        return apiClient.post("/" + targetId + "/feed", accessToken, body, JsonNode.class);
    }
    
    /**
     * Update a post
     * @param postId Post ID
     * @param message Updated message
     * @param accessToken Access token
     * @return Updated post
     */
    public JsonNode updatePost(String postId, String message, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        
        return apiClient.post("/" + postId, accessToken, body, JsonNode.class);
    }
    
    /**
     * Delete a post
     * @param postId Post ID
     * @param accessToken Access token
     * @return true if deleted successfully
     */
    public boolean deletePost(String postId, String accessToken) {
        return apiClient.delete("/" + postId, accessToken);
    }
    
    /**
     * Get comments for a post
     * @param postId Post ID
     * @param accessToken Access token
     * @param limit Number of comments to retrieve
     * @param offset Offset for pagination
     * @return List of comments
     */
    public JsonNode getComments(String postId, String accessToken, int limit, int offset) {
        Map<String, String> params = new HashMap<>();
        params.put("fields", "id,message,created_time,from,parent");
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        
        return apiClient.get("/" + postId + "/comments", accessToken, JsonNode.class, params);
    }
    
    /**
     * Create a comment
     * @param postId Post ID
     * @param message Comment message
     * @param accessToken Access token
     * @return Created comment
     */
    public JsonNode createComment(String postId, String message, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        
        return apiClient.post("/" + postId + "/comments", accessToken, body, JsonNode.class);
    }
    
    /**
     * Delete a comment
     * @param commentId Comment ID
     * @param accessToken Access token
     * @return true if deleted successfully
     */
    public boolean deleteComment(String commentId, String accessToken) {
        return apiClient.delete("/" + commentId, accessToken);
    }
    
    /**
     * Hide a comment
     * @param commentId Comment ID
     * @param accessToken Access token
     * @return true if hidden successfully
     */
    public boolean hideComment(String commentId, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("is_hidden", true);
        
        try {
            apiClient.post("/" + commentId, accessToken, body, JsonNode.class);
            return true;
        } catch (Exception e) {
            log.error("Error hiding comment: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get post insights
     * @param postId Post ID
     * @param accessToken Access token
     * @return Post insights
     */
    public JsonNode getPostInsights(String postId, String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("metric", "post_impressions,post_impressions_unique,post_reactions_by_type_total,post_clicks,post_engaged_users");
        
        return apiClient.get("/" + postId + "/insights", accessToken, JsonNode.class, params);
    }
    
    /**
     * Get page insights
     * @param pageId Page ID
     * @param accessToken Access token
     * @return Page insights
     */
    public JsonNode getPageInsights(String pageId, String accessToken) {
        Map<String, String> params = new HashMap<>();
        params.put("metric", "page_impressions,page_impressions_unique,page_engaged_users,page_fans,page_fan_adds");
        params.put("period", "day");
        params.put("date_preset", "last_30_days");
        
        return apiClient.get("/" + pageId + "/insights", accessToken, JsonNode.class, params);
    }
}
