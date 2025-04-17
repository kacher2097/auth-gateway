package com.authenhub.controller;

import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.FreeProxyDto;
import com.authenhub.dto.request.ProxyFilterRequest;
import com.authenhub.filter.JwtService;
import com.authenhub.service.interfaces.IFreeProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for proxy operations
 */
@RestController
@RequestMapping("/api/v1/proxies")
@RequiredArgsConstructor
public class ProxyController {

    private final IFreeProxyService proxyService;
    private final JwtService jwtService;

    /**
     * Get all proxies with filtering
     *
     * @param request proxy filter request
     * @return list of proxies
     */
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse> getProxies(@RequestBody ProxyFilterRequest request) {
        List<FreeProxyDto.Response> proxies;
        
        if (request.getActive() != null && request.getActive()) {
            proxies = proxyService.getActiveProxies();
        } else if (request.getProtocol() != null && !request.getProtocol().isEmpty()) {
            proxies = proxyService.getProxiesByProtocol(request.getProtocol());
        } else if (request.getCountry() != null && !request.getCountry().isEmpty()) {
            proxies = proxyService.getProxiesByCountry(request.getCountry());
        } else if (request.getMaxResponseTime() != null) {
            proxies = proxyService.getFastProxies(request.getMaxResponseTime());
        } else if (request.getMinUptime() != null) {
            proxies = proxyService.getReliableProxies(request.getMinUptime());
        } else {
            proxies = proxyService.getAllProxies();
        }
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies retrieved successfully")
                .data(proxies)
                .build());
    }
    
    /**
     * Get proxy by id
     *
     * @param id proxy id
     * @return proxy
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProxyById(@PathVariable String id) {
        FreeProxyDto.Response proxy = proxyService.getProxyById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy retrieved successfully")
                .data(proxy)
                .build());
    }
    
    /**
     * Create a new proxy
     *
     * @param request proxy request
     * @param token auth token
     * @return created proxy
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProxy(
            @Valid @RequestBody FreeProxyDto.Request request,
            @RequestHeader("Authorization") String token) {

        // Extract username from token
        String jwt = token.substring(7); // Remove "Bearer " prefix
        String username = jwtService.extractUsername(jwt);

        FreeProxyDto.Response createdProxy = proxyService.createProxy(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .success(true)
                .message("Proxy created successfully")
                .data(createdProxy)
                .build());
    }
    
    /**
     * Update a proxy
     *
     * @param id proxy id
     * @param request proxy request
     * @return updated proxy
     */
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
    
    /**
     * Delete a proxy
     *
     * @param id proxy id
     * @return response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProxy(@PathVariable String id) {
        proxyService.deleteProxy(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy deleted successfully")
                .build());
    }
    
    /**
     * Check proxy by id
     *
     * @param id proxy id
     * @return check result
     */
    @PostMapping("/{id}/check")
    public ResponseEntity<ApiResponse> checkProxy(@PathVariable String id) {
        FreeProxyDto.CheckResult result = proxyService.checkProxyById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxy check completed")
                .data(result)
                .build());
    }
    
    /**
     * Import proxies from file
     *
     * @param file proxy file
     * @param fileType file type
     * @return import result
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> importProxies(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType) {

        FreeProxyDto.ImportResult result = proxyService.importProxies(file, fileType);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Proxies imported successfully")
                .data(result)
                .build());
    }
}
