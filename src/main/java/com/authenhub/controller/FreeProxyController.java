package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.proxy.*;
import com.authenhub.service.FreeProxyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxies")
@RequiredArgsConstructor
@Tag(name = "Free Proxy", description = "API để quản lý và sử dụng các proxy miễn phí")
public class FreeProxyController {

    private final FreeProxyService proxyService;

    @PostMapping
    @Operation(summary = "Lấy danh sách tất cả proxy",
            description = "Trả về danh sách tất cả proxy trong hệ thống.")
    public ApiResponse<?> getAllProxiesPost() {
        List<ProxyResponse> proxies = proxyService.getAllProxies();
        return ApiResponse.success(proxies);
    }

//    @GetMapping("/active")
//    public ApiResponse<?> getActiveProxies() {
//        List<FreeProxyDto.Response> proxies = proxyService.getActiveProxies();
//        return ApiResponse.success(proxies);
//    }

    @PostMapping("/active")
    @Operation(summary = "Lấy danh sách proxy đang hoạt động",
            description = "Trả về danh sách các proxy đang hoạt động trong hệ thống.")
    public ApiResponse<?> getActiveProxiesPost() {
        List<ProxyResponse> proxies = proxyService.getActiveProxies();
        return ApiResponse.success(proxies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy proxy theo ID",
            description = "Trả về thông tin chi tiết của proxy dựa trên ID.")
    public ApiResponse<?> getProxyById(@PathVariable String id) {
        ProxyResponse proxy = proxyService.getProxyById(id);
        return ApiResponse.success(proxy);
    }

    @PostMapping("/{id}/get")
    @Operation(summary = "Lấy proxy theo ID (POST)",
            description = "Trả về thông tin chi tiết của proxy dựa trên ID sử dụng phương thức POST.")
    public ApiResponse<?> getProxyByIdPost(@PathVariable String id) {
        return getProxyById(id);
    }

    @PostMapping("/create")
    @Operation(summary = "Tạo proxy mới",
            description = "Tạo một proxy mới trong hệ thống.")
    public ApiResponse<?> createProxy(
            @Valid @RequestBody ProxyRequest request,
            @RequestHeader("Authorization") String token) {
        ProxyResponse createdProxy = proxyService.createProxy(request, token);
        return ApiResponse.success(createdProxy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật proxy",
            description = "Cập nhật thông tin của proxy dựa trên ID.")
    public ApiResponse<?> updateProxy(
            @PathVariable String id,
            @Valid @RequestBody ProxyRequest request) {

        ProxyResponse updatedProxy = proxyService.updateProxy(id, request);
        return ApiResponse.success(updatedProxy);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa proxy",
            description = "Xóa proxy dựa trên ID.")
    public ApiResponse<?> deleteProxy(@PathVariable String id) {
        proxyService.deleteProxy(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/check")
    @Operation(summary = "Kiểm tra proxy",
            description = "Kiểm tra tình trạng hoạt động của proxy dựa trên ID.")
    public ApiResponse<?> checkProxy(@PathVariable String id) {
        CheckResult result = proxyService.checkProxyById(id);
        return ApiResponse.success(result);
    }

    @GetMapping("/protocol/{protocol}")
    @Operation(summary = "Lấy proxy theo giao thức",
            description = "Trả về danh sách proxy theo giao thức (HTTP, HTTPS, SOCKS4, SOCKS5).")
    public ApiResponse<?> getProxiesByProtocol(@PathVariable String protocol) {
        List<ProxyResponse> proxies = proxyService.getProxiesByProtocol(protocol);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/protocol")
    @Operation(summary = "Lấy proxy theo giao thức (POST)",
            description = "Trả về danh sách proxy theo giao thức sử dụng phương thức POST.")
    public ApiResponse<?> getProxiesByProtocolPost(@RequestBody ProxyProtocolRequest request) {
        List<ProxyResponse> proxies = proxyService.getProxiesByProtocol(request.getProtocol());
        return ApiResponse.success(proxies);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Lấy proxy theo quốc gia",
            description = "Trả về danh sách proxy theo quốc gia.")
    public ApiResponse<?> getProxiesByCountry(@PathVariable String country) {
        List<ProxyResponse> proxies = proxyService.getProxiesByCountry(country);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/country")
    @Operation(summary = "Lấy proxy theo quốc gia (POST)",
            description = "Trả về danh sách proxy theo quốc gia sử dụng phương thức POST.")
    public ApiResponse<?> getProxiesByCountryPost(@RequestBody ProxyCountryRequest request) {
        List<ProxyResponse> proxies = proxyService.getProxiesByCountry(request.getCountry());
        return ApiResponse.success(proxies);
    }

    @PostMapping("/fast")
    @Operation(summary = "Lấy proxy nhanh",
            description = "Trả về danh sách proxy có thời gian phản hồi nhanh.")
    public ApiResponse<?> getFastProxiesPost(@RequestBody FastProxyRequest request) {
        int maxResponseTime = request.getMaxResponseTime() != null ? request.getMaxResponseTime() : 1000;
        List<ProxyResponse> proxies = proxyService.getFastProxies(maxResponseTime);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/reliable")
    @Operation(summary = "Lấy proxy đáng tin cậy",
            description = "Trả về danh sách proxy có tỉ lệ hoạt động cao.")
    public ApiResponse<?> getReliableProxiesPost(@RequestBody ReliableProxyRequest request) {
        double minUptime = request.getMinUptime() != null ? request.getMinUptime() : 90.0;
        List<ProxyResponse> proxies = proxyService.getReliableProxies(minUptime);
        return ApiResponse.success(proxies);
    }

//    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ApiResponse<?> importProxies(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("fileType") String fileType,
//            @RequestHeader("Authorization") String token) {
//
//        // Extract username from token
//        String jwt = token.substring(7); // Remove "Bearer " prefix
//        String username = jwtService.extractUsername(jwt);
//
//        FreeProxyDto.ImportResult result = proxyService.importProxiesFromFile(file, fileType, username);
//
//        return ResponseEntity.ok(ApiResponse.builder()
//        return ApiResponse.success(result);
//    }
}
