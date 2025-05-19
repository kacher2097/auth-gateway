package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.facebook.page.FacebookPageRequest;
import com.authenhub.bean.facebook.page.FacebookPageResponse;
import com.authenhub.service.interfaces.IFacebookPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facebook/pages")
@RequiredArgsConstructor
public class FacebookPageController {

    private final IFacebookPageService pageService;

    @GetMapping
    public ApiResponse<?> getUserPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.getUserPages(userId);
        return ApiResponse.success(pages);
    }

    @GetMapping("/{pageId}")
    public ApiResponse<?> getPageDetails(
            @PathVariable String pageId,
            @RequestParam String userId) {
        FacebookPageResponse page = pageService.getPageDetails(pageId, userId);
        return ApiResponse.success(page);
    }

    @PutMapping("/{pageId}")
    public ApiResponse<?> updatePageInfo(
            @PathVariable String pageId,
            @RequestParam String userId,
            @RequestBody FacebookPageRequest request) {
        FacebookPageResponse page = pageService.updatePageInfo(pageId, request, userId);
        return ApiResponse.success(page);
    }

    @PostMapping("/sync")
    public ApiResponse<?> syncPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.syncPages(userId);
        return ApiResponse.success(pages);
    }
}
