package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.facebook.page.FacebookPageRequest;
import com.authenhub.bean.facebook.page.FacebookPageResponse;
import com.authenhub.service.interfaces.IFacebookPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facebook/pages")
@RequiredArgsConstructor
@Tag(name = "Facebook Pages", description = "API để quản lý trang Facebook")
public class FacebookPageController {

    private final IFacebookPageService pageService;

    @GetMapping
    @Operation(summary = "Lấy danh sách trang của người dùng",
            description = "Lấy danh sách các trang Facebook mà người dùng quản lý.")
    public ApiResponse<?> getUserPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.getUserPages(userId);
        return ApiResponse.success(pages);
    }

    @GetMapping("/{pageId}")
    @Operation(summary = "Lấy chi tiết trang",
            description = "Lấy thông tin chi tiết của một trang Facebook dựa trên ID.")
    public ApiResponse<?> getPageDetails(
            @PathVariable String pageId,
            @RequestParam String userId) {
        FacebookPageResponse page = pageService.getPageDetails(pageId, userId);
        return ApiResponse.success(page);
    }

    @PutMapping("/{pageId}")
    @Operation(summary = "Cập nhật thông tin trang",
            description = "Cập nhật thông tin của một trang Facebook dựa trên ID.")
    public ApiResponse<?> updatePageInfo(
            @PathVariable String pageId,
            @RequestParam String userId,
            @RequestBody FacebookPageRequest request) {
        FacebookPageResponse page = pageService.updatePageInfo(pageId, request, userId);
        return ApiResponse.success(page);
    }

    @PostMapping("/sync")
    @Operation(summary = "Đồng bộ trang",
            description = "Đồng bộ danh sách các trang Facebook của người dùng từ Facebook.")
    public ApiResponse<?> syncPages(@RequestParam String userId) {
        List<FacebookPageResponse> pages = pageService.syncPages(userId);
        return ApiResponse.success(pages);
    }
}
