package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.proxy.*;
import com.authenhub.service.FreeProxyService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proxies")
@RequiredArgsConstructor
public class FreeProxyController {

    private final FreeProxyService proxyService;

    @PostMapping
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
    public ApiResponse<?> getActiveProxiesPost() {
        List<ProxyResponse> proxies = proxyService.getActiveProxies();
        return ApiResponse.success(proxies);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getProxyById(@PathVariable String id) {
        ProxyResponse proxy = proxyService.getProxyById(id);
        return ApiResponse.success(proxy);
    }

    @PostMapping("/{id}/get")
    public ApiResponse<?> getProxyByIdPost(@PathVariable String id) {
        return getProxyById(id);
    }

    @PostMapping("/create")
    public ApiResponse<?> createProxy(
            @Valid @RequestBody ProxyRequest request,
            @RequestHeader("Authorization") String token) {
        ProxyResponse createdProxy = proxyService.createProxy(request, token);
        return ApiResponse.success(createdProxy);
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateProxy(
            @PathVariable String id,
            @Valid @RequestBody ProxyRequest request) {

        ProxyResponse updatedProxy = proxyService.updateProxy(id, request);
        return ApiResponse.success(updatedProxy);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProxy(@PathVariable String id) {
        proxyService.deleteProxy(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/check")
    public ApiResponse<?> checkProxy(@PathVariable String id) {
        CheckResult result = proxyService.checkProxyById(id);
        return ApiResponse.success(result);
    }

    @GetMapping("/protocol/{protocol}")
    public ApiResponse<?> getProxiesByProtocol(@PathVariable String protocol) {
        List<ProxyResponse> proxies = proxyService.getProxiesByProtocol(protocol);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/protocol")
    public ApiResponse<?> getProxiesByProtocolPost(@RequestBody ProxyProtocolRequest request) {
        List<ProxyResponse> proxies = proxyService.getProxiesByProtocol(request.getProtocol());
        return ApiResponse.success(proxies);
    }

    @GetMapping("/country/{country}")
    public ApiResponse<?> getProxiesByCountry(@PathVariable String country) {
        List<ProxyResponse> proxies = proxyService.getProxiesByCountry(country);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/country")
    public ApiResponse<?> getProxiesByCountryPost(@RequestBody ProxyCountryRequest request) {
        List<ProxyResponse> proxies = proxyService.getProxiesByCountry(request.getCountry());
        return ApiResponse.success(proxies);
    }

    @PostMapping("/fast")
    public ApiResponse<?> getFastProxiesPost(@RequestBody FastProxyRequest request) {
        int maxResponseTime = request.getMaxResponseTime() != null ? request.getMaxResponseTime() : 1000;
        List<ProxyResponse> proxies = proxyService.getFastProxies(maxResponseTime);
        return ApiResponse.success(proxies);
    }

    @PostMapping("/reliable")
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
