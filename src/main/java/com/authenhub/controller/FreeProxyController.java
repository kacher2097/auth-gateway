package com.authenhub.controller;

import com.authenhub.bean.proxy.FastProxyRequest;
import com.authenhub.bean.proxy.ProxyCountryRequest;
import com.authenhub.bean.proxy.ProxyProtocolRequest;
import com.authenhub.bean.proxy.ReliableProxyRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.FreeProxyDto;
import com.authenhub.filter.JwtService;
import com.authenhub.service.FreeProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/proxies")
@RequiredArgsConstructor
public class FreeProxyController {

    private final FreeProxyService proxyService;
    private final JwtService jwtService;

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> getAllProxiesPost() {
        List<FreeProxyDto.Response> proxies = proxyService.getAllProxies();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }

//    @GetMapping("/active")
//    public ResponseEntity<ApiResponse> getActiveProxies() {
//        List<FreeProxyDto.Response> proxies = proxyService.getActiveProxies();
//        return ResponseEntity.ok(ApiResponse.builder()
//                .success(true)
//                .message("Active proxies retrieved successfully")
//                .data(proxies)
//                .build());
//    }

    @PostMapping("/active")
    public ResponseEntity<ApiResponse> getActiveProxiesPost() {
        List<FreeProxyDto.Response> proxies = proxyService.getActiveProxies();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Active proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProxyById(@PathVariable String id) {
        FreeProxyDto.Response proxy = proxyService.getProxyById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy retrieved successfully")
                .data(proxy)
                .build());
    }

    @PostMapping("/{id}/get")
    public ResponseEntity<ApiResponse> getProxyByIdPost(@PathVariable String id) {
        return getProxyById(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createProxy(
            @Valid @RequestBody FreeProxyDto.Request request,
            @RequestHeader("Authorization") String token) {
        FreeProxyDto.Response createdProxy = proxyService.createProxy(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Proxy created successfully")
                .data(createdProxy)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProxy(
            @PathVariable String id,
            @Valid @RequestBody FreeProxyDto.Request request) {

        FreeProxyDto.Response updatedProxy = proxyService.updateProxy(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy updated successfully")
                .data(updatedProxy)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProxy(@PathVariable String id) {
        proxyService.deleteProxy(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy deleted successfully")
                .build());
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<ApiResponse> checkProxy(@PathVariable String id) {
        FreeProxyDto.CheckResult result = proxyService.checkProxyById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy check completed")
                .data(result)
                .build());
    }

    @GetMapping("/protocol/{protocol}")
    public ResponseEntity<ApiResponse> getProxiesByProtocol(@PathVariable String protocol) {
        List<FreeProxyDto.Response> proxies = proxyService.getProxiesByProtocol(protocol);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @PostMapping("/protocol")
    public ResponseEntity<ApiResponse> getProxiesByProtocolPost(@RequestBody ProxyProtocolRequest request) {
        List<FreeProxyDto.Response> proxies = proxyService.getProxiesByProtocol(request.getProtocol());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<ApiResponse> getProxiesByCountry(@PathVariable String country) {
        List<FreeProxyDto.Response> proxies = proxyService.getProxiesByCountry(country);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @PostMapping("/country")
    public ResponseEntity<ApiResponse> getProxiesByCountryPost(@RequestBody ProxyCountryRequest request) {
        List<FreeProxyDto.Response> proxies = proxyService.getProxiesByCountry(request.getCountry());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @PostMapping("/fast")
    public ResponseEntity<ApiResponse> getFastProxiesPost(@RequestBody FastProxyRequest request) {
        int maxResponseTime = request.getMaxResponseTime() != null ? request.getMaxResponseTime() : 1000;
        List<FreeProxyDto.Response> proxies = proxyService.getFastProxies(maxResponseTime);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Fast proxies retrieved successfully")
                .data(proxies)
                .build());
    }

    @PostMapping("/reliable")
    public ResponseEntity<ApiResponse> getReliableProxiesPost(@RequestBody ReliableProxyRequest request) {
        double minUptime = request.getMinUptime() != null ? request.getMinUptime() : 90.0;
        List<FreeProxyDto.Response> proxies = proxyService.getReliableProxies(minUptime);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Reliable proxies retrieved successfully")
                .data(proxies)
                .build());
    }

//    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN')")
//    public ResponseEntity<ApiResponse> importProxies(
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
//                .success(true)
//                .message(String.format("Import completed: %d processed, %d successful, %d failed",
//                        result.getTotalProcessed(), result.getSuccessCount(), result.getFailCount()))
//                .data(result)
//                .build());
//    }
}
