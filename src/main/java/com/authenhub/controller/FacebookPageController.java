package com.authenhub.controller;

import com.authenhub.bean.common.SimpleApiResponse;
import com.authenhub.bean.facebook.page.FacebookPageRequest;
import com.authenhub.bean.facebook.page.FacebookPageResponse;
import com.authenhub.service.interfaces.IFacebookPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facebook/pages")
@RequiredArgsConstructor
public class FacebookPageController {
    
    private final IFacebookPageService pageService;
    
    @GetMapping
    public ResponseEntity<List<FacebookPageResponse>> getUserPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.getUserPages(userId);
        return ResponseEntity.ok(pages);
    }
    
    @GetMapping("/{pageId}")
    public ResponseEntity<FacebookPageResponse> getPageDetails(
            @PathVariable String pageId,
            @RequestParam String userId) {
        FacebookPageResponse page = pageService.getPageDetails(pageId, userId);
        return ResponseEntity.ok(page);
    }
    
    @PutMapping("/{pageId}")
    public ResponseEntity<FacebookPageResponse> updatePageInfo(
            @PathVariable String pageId,
            @RequestParam String userId,
            @RequestBody FacebookPageRequest request) {
        FacebookPageResponse page = pageService.updatePageInfo(pageId, request, userId);
        return ResponseEntity.ok(page);
    }
    
    @PostMapping("/sync")
    public ResponseEntity<SimpleApiResponse> syncPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.syncPages(userId);
        
        SimpleApiResponse response = SimpleApiResponse.builder()
                .success(true)
                .message("Pages synchronized successfully")
                .data(pages)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
