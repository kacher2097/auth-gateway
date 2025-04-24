package com.authenhub.service.impl;

import com.authenhub.bean.facebook.auth.FacebookTokenInfo;
import com.authenhub.bean.facebook.page.FacebookPageRequest;
import com.authenhub.bean.facebook.page.FacebookPageResponse;
import com.authenhub.entity.mongo.FacebookPage;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.FacebookPageRepository;
import com.authenhub.service.client.FacebookGraphApiWrapper;
import com.authenhub.service.interfaces.IFacebookAuthService;
import com.authenhub.service.interfaces.IFacebookPageService;
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
public class FacebookPageService implements IFacebookPageService {
    private final FacebookPageRepository pageRepository;
    private final IFacebookAuthService authService;
    private final FacebookGraphApiWrapper graphApiWrapper;
    
    @Override
    public List<FacebookPageResponse> getUserPages(String userId) {
        return pageRepository.findByUserId(userId).stream()
                .map(FacebookPageResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public FacebookPageResponse getPageDetails(String pageId, String userId) {
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + pageId));
        
        return FacebookPageResponse.fromEntity(page);
    }
    
    @Override
    public FacebookPageResponse updatePageInfo(String pageId, FacebookPageRequest request, String userId) {
        FacebookPage page = pageRepository.findByUserIdAndPageId(userId, pageId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found with id: " + pageId));
        
        // Cập nhật thông tin trang
        if (request.getName() != null) {
            page.setPageName(request.getName());
        }
        
        if (request.getPictureUrl() != null) {
            page.setPictureUrl(request.getPictureUrl());
        }
        
        page.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        FacebookPage updatedPage = pageRepository.save(page);
        return FacebookPageResponse.fromEntity(updatedPage);
    }
    
    @Override
    public List<FacebookPageResponse> syncPages(String userId) {
        // Lấy token của người dùng
        FacebookTokenInfo tokenInfo = authService.getUserToken(userId);
        
        // Lấy danh sách trang từ Facebook
        List<JsonNode> pages = graphApiWrapper.getUserPages(tokenInfo.getAccessToken());
        
        // Xóa các trang cũ
        List<FacebookPage> existingPages = pageRepository.findByUserId(userId);
        pageRepository.deleteAll(existingPages);
        
        // Lưu các trang mới
        List<FacebookPage> newPages = new ArrayList<>();
        
        for (JsonNode page : pages) {
            FacebookPage newPage = FacebookPage.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .pageId(page.get("id").asText())
                    .pageName(page.get("name").asText())
                    .pageToken(page.get("access_token").asText())
                    .category(page.get("category").asText())
                    .pictureUrl(page.has("picture") && page.get("picture").has("data") ? 
                            page.get("picture").get("data").get("url").asText() : null)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            
            newPages.add(newPage);
        }
        
        pageRepository.saveAll(newPages);
        
        // Trả về danh sách trang đã đồng bộ
        return newPages.stream()
                .map(FacebookPageResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
