package com.authenhub.service.impl;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.bean.facebook.stats.FacebookEngagementResponse;
import com.authenhub.bean.facebook.stats.FacebookPageStatsResponse;
import com.authenhub.bean.facebook.stats.FacebookPostStatsResponse;
import com.authenhub.entity.mongo.FacebookPage;
import com.authenhub.entity.mongo.FacebookPost;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.FacebookPageRepository;
import com.authenhub.repository.FacebookPostRepository;
import com.authenhub.service.client.FacebookGraphApiWrapper;
import com.authenhub.service.interfaces.IFacebookAuthService;
import com.authenhub.service.interfaces.IFacebookStatsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookStatsService implements IFacebookStatsService {
    private final FacebookPostRepository postRepository;
    private final FacebookPageRepository pageRepository;
    private final IFacebookAuthService authService;
    private final FacebookGraphApiWrapper graphApiWrapper;
    
    @Override
    public FacebookPostStatsResponse getPostStats(String postId, String userId) {
        // Kiểm tra xem bài viết có trong database không
        FacebookPost post = postRepository.findByUserIdAndPostId(userId, postId).orElse(null);
        
        // Lấy token
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        
        // Lấy thống kê từ Facebook API
        JsonNode insights = graphApiWrapper.getPostInsights(postId, tokenInfo.getAccessToken());
        
        // Xử lý dữ liệu thống kê
        int reach = 0;
        int engagement = 0;
        int clicks = 0;
        
        if (insights.has("data") && insights.get("data").isArray()) {
            for (JsonNode metric : insights.get("data")) {
                String name = metric.get("name").asText();
                if (name.equals("post_impressions_unique") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    reach = metric.get("values").get(0).get("value").asInt();
                } else if (name.equals("post_engaged_users") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    engagement = metric.get("values").get(0).get("value").asInt();
                } else if (name.equals("post_clicks") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    clicks = metric.get("values").get(0).get("value").asInt();
                }
            }
        }
        
        // Lấy thông tin bài viết từ Facebook API để cập nhật số lượng like, comment, share
        JsonNode postDetails = graphApiWrapper.getPostDetails(postId, tokenInfo.getAccessToken());
        
        int likesCount = 0;
        int commentsCount = 0;
        int sharesCount = 0;
        
        if (postDetails.has("likes") && postDetails.get("likes").has("summary") && postDetails.get("likes").get("summary").has("total_count")) {
            likesCount = postDetails.get("likes").get("summary").get("total_count").asInt();
        }
        
        if (postDetails.has("comments") && postDetails.get("comments").has("summary") && postDetails.get("comments").get("summary").has("total_count")) {
            commentsCount = postDetails.get("comments").get("summary").get("total_count").asInt();
        }
        
        if (postDetails.has("shares") && postDetails.get("shares").has("count")) {
            sharesCount = postDetails.get("shares").get("count").asInt();
        }
        
        // Cập nhật thông tin bài viết trong database nếu có
        if (post != null) {
            post.setLikesCount(likesCount);
            post.setCommentsCount(commentsCount);
            post.setSharesCount(sharesCount);
            post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            postRepository.save(post);
        }
        
        return FacebookPostStatsResponse.builder()
                .postId(postId)
                .likesCount(likesCount)
                .commentsCount(commentsCount)
                .sharesCount(sharesCount)
                .reach(reach)
                .engagement(engagement)
                .clicks(clicks)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
    }
    
    @Override
    public FacebookPageStatsResponse getPageStats(String pageId, String userId) {
        // Kiểm tra xem trang có trong database không
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + pageId));
        
        // Lấy thống kê từ Facebook API
        JsonNode insights = graphApiWrapper.getPageInsights(pageId, page.getPageToken());
        
        // Xử lý dữ liệu thống kê
        int totalReach = 0;
        int totalEngagement = 0;
        int fanCount = 0;
        int newFans = 0;
        
        if (insights.has("data") && insights.get("data").isArray()) {
            for (JsonNode metric : insights.get("data")) {
                String name = metric.get("name").asText();
                if (name.equals("page_impressions_unique") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    totalReach = metric.get("values").get(0).get("value").asInt();
                } else if (name.equals("page_engaged_users") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    totalEngagement = metric.get("values").get(0).get("value").asInt();
                } else if (name.equals("page_fans") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    fanCount = metric.get("values").get(0).get("value").asInt();
                } else if (name.equals("page_fan_adds") && metric.has("values") && metric.get("values").isArray() && metric.get("values").size() > 0) {
                    newFans = metric.get("values").get(0).get("value").asInt();
                }
            }
        }
        
        // Lấy số lượng bài viết
        int postCount = postRepository.findByUserIdAndPageId(userId, pageId).size();
        
        return FacebookPageStatsResponse.builder()
                .pageId(pageId)
                .fanCount(fanCount)
                .newFans(newFans)
                .postCount(postCount)
                .totalReach(totalReach)
                .totalEngagement(totalEngagement)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();
    }
    
    @Override
    public FacebookEngagementResponse getEngagementData(String id, String type, Timestamp startDate, Timestamp endDate, String userId) {
        if (type.equals("page")) {
            return getPageEngagementData(id, startDate, endDate, userId);
        } else if (type.equals("post")) {
            return getPostEngagementData(id, startDate, endDate, userId);
        } else {
            throw new IllegalArgumentException("Invalid type. Must be 'page' or 'post'");
        }
    }
    
    private FacebookEngagementResponse getPageEngagementData(String pageId, Timestamp startDate, Timestamp endDate, String userId) {
        // Kiểm tra xem trang có trong database không
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + pageId));
        
        // Lấy thống kê từ Facebook API
        JsonNode insights = graphApiWrapper.getPageInsights(pageId, page.getPageToken());
        
        // Xử lý dữ liệu thống kê
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        Map<String, Integer> totalStats = new HashMap<>();
        totalStats.put("reach", 0);
        totalStats.put("engagement", 0);
        totalStats.put("newFans", 0);
        
        if (insights.has("data") && insights.get("data").isArray()) {
            // Tạo map để lưu trữ dữ liệu theo ngày
            Map<String, Map<String, Object>> dailyData = new HashMap<>();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (JsonNode metric : insights.get("data")) {
                String name = metric.get("name").asText();
                
                if (metric.has("values") && metric.get("values").isArray()) {
                    for (JsonNode value : metric.get("values")) {
                        if (value.has("end_time") && value.has("value")) {
                            String dateStr = value.get("end_time").asText().substring(0, 10);
                            int metricValue = value.get("value").asInt();
                            
                            // Kiểm tra xem ngày có nằm trong khoảng thời gian không
                            try {
                                Date date = dateFormat.parse(dateStr);
                                if (date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
                                    // Thêm dữ liệu vào map
                                    if (!dailyData.containsKey(dateStr)) {
                                        dailyData.put(dateStr, new HashMap<>());
                                        dailyData.get(dateStr).put("date", dateStr);
                                    }
                                    
                                    if (name.equals("page_impressions_unique")) {
                                        dailyData.get(dateStr).put("reach", metricValue);
                                        totalStats.put("reach", totalStats.get("reach") + metricValue);
                                    } else if (name.equals("page_engaged_users")) {
                                        dailyData.get(dateStr).put("engagement", metricValue);
                                        totalStats.put("engagement", totalStats.get("engagement") + metricValue);
                                    } else if (name.equals("page_fan_adds")) {
                                        dailyData.get(dateStr).put("newFans", metricValue);
                                        totalStats.put("newFans", totalStats.get("newFans") + metricValue);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Error parsing date: {}", e.getMessage());
                            }
                        }
                    }
                }
            }
            
            // Chuyển map thành list
            for (Map<String, Object> data : dailyData.values()) {
                dailyStats.add(data);
            }
            
            // Sắp xếp theo ngày
            dailyStats.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        }
        
        return FacebookEngagementResponse.builder()
                .id(pageId)
                .type("page")
                .dailyStats(dailyStats)
                .totalStats(totalStats)
                .build();
    }
    
    private FacebookEngagementResponse getPostEngagementData(String postId, Timestamp startDate, Timestamp endDate, String userId) {
        // Kiểm tra xem bài viết có trong database không
        FacebookPost post = postRepository.findByUserIdAndPostId(userId, postId).orElse(null);
        
        // Lấy token
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        
        // Lấy thống kê từ Facebook API
        JsonNode insights = graphApiWrapper.getPostInsights(postId, tokenInfo.getAccessToken());
        
        // Xử lý dữ liệu thống kê
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        Map<String, Integer> totalStats = new HashMap<>();
        totalStats.put("reach", 0);
        totalStats.put("engagement", 0);
        totalStats.put("clicks", 0);
        
        if (insights.has("data") && insights.get("data").isArray()) {
            // Tạo map để lưu trữ dữ liệu theo ngày
            Map<String, Map<String, Object>> dailyData = new HashMap<>();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (JsonNode metric : insights.get("data")) {
                String name = metric.get("name").asText();
                
                if (metric.has("values") && metric.get("values").isArray()) {
                    for (JsonNode value : metric.get("values")) {
                        if (value.has("end_time") && value.has("value")) {
                            String dateStr = value.get("end_time").asText().substring(0, 10);
                            int metricValue = value.get("value").asInt();
                            
                            // Kiểm tra xem ngày có nằm trong khoảng thời gian không
                            try {
                                Date date = dateFormat.parse(dateStr);
                                if (date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
                                    // Thêm dữ liệu vào map
                                    if (!dailyData.containsKey(dateStr)) {
                                        dailyData.put(dateStr, new HashMap<>());
                                        dailyData.get(dateStr).put("date", dateStr);
                                    }
                                    
                                    if (name.equals("post_impressions_unique")) {
                                        dailyData.get(dateStr).put("reach", metricValue);
                                        totalStats.put("reach", totalStats.get("reach") + metricValue);
                                    } else if (name.equals("post_engaged_users")) {
                                        dailyData.get(dateStr).put("engagement", metricValue);
                                        totalStats.put("engagement", totalStats.get("engagement") + metricValue);
                                    } else if (name.equals("post_clicks")) {
                                        dailyData.get(dateStr).put("clicks", metricValue);
                                        totalStats.put("clicks", totalStats.get("clicks") + metricValue);
                                    }
                                }
                            } catch (Exception e) {
                                log.error("Error parsing date: {}", e.getMessage());
                            }
                        }
                    }
                }
            }
            
            // Chuyển map thành list
            for (Map<String, Object> data : dailyData.values()) {
                dailyStats.add(data);
            }
            
            // Sắp xếp theo ngày
            dailyStats.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));
        }
        
        // Lấy thông tin bài viết từ Facebook API để cập nhật số lượng like, comment, share
        JsonNode postDetails = graphApiWrapper.getPostDetails(postId, tokenInfo.getAccessToken());
        
        int likesCount = 0;
        int commentsCount = 0;
        int sharesCount = 0;
        
        if (postDetails.has("likes") && postDetails.get("likes").has("summary") && postDetails.get("likes").get("summary").has("total_count")) {
            likesCount = postDetails.get("likes").get("summary").get("total_count").asInt();
        }
        
        if (postDetails.has("comments") && postDetails.get("comments").has("summary") && postDetails.get("comments").get("summary").has("total_count")) {
            commentsCount = postDetails.get("comments").get("summary").get("total_count").asInt();
        }
        
        if (postDetails.has("shares") && postDetails.get("shares").has("count")) {
            sharesCount = postDetails.get("shares").get("count").asInt();
        }
        
        totalStats.put("likes", likesCount);
        totalStats.put("comments", commentsCount);
        totalStats.put("shares", sharesCount);
        
        // Cập nhật thông tin bài viết trong database nếu có
        if (post != null) {
            post.setLikesCount(likesCount);
            post.setCommentsCount(commentsCount);
            post.setSharesCount(sharesCount);
            post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            postRepository.save(post);
        }
        
        return FacebookEngagementResponse.builder()
                .id(postId)
                .type("post")
                .dailyStats(dailyStats)
                .totalStats(totalStats)
                .build();
    }
}
