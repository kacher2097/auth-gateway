package com.authenhub.service.impl;

import com.authenhub.bean.tool.apitester.ApiTestRequest;
import com.authenhub.bean.tool.apitester.ApiTestResponse;
import com.authenhub.bean.tool.apitester.RequestHeader;
import com.authenhub.entity.ApiTest;
import com.authenhub.repository.ApiTestRepository;
import com.authenhub.service.interfaces.IApiTesterService;
import com.authenhub.utils.TimestampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiTesterService implements IApiTesterService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ApiTestRepository apiTestRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ApiTestResponse testApi(ApiTestRequest request) {
        log.info("Testing API: {} {}", request.getMethod(), request.getUrl());
        
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            if (request.getHeaders() != null) {
                for (RequestHeader header : request.getHeaders()) {
                    if (header.getKey() != null && !header.getKey().isEmpty()) {
                        headers.add(header.getKey(), header.getValue());
                    }
                }
            }
            
            // Set content type if provided
            if (request.getContentType() != null && !request.getContentType().isEmpty()) {
                headers.add("Content-Type", request.getContentType());
            }
            
            // Create the request entity
            HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);
            
            // Measure response time
            Instant start = Instant.now();
            
            // Execute the request
            ResponseEntity<String> response = restTemplate.exchange(
                    request.getUrl(),
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    String.class
            );
            
            Instant end = Instant.now();
            long responseTimeMs = Duration.between(start, end).toMillis();
            
            // Convert response headers to map
            Map<String, String> responseHeaders = new HashMap<>();
            response.getHeaders().forEach((key, value) -> {
                responseHeaders.put(key, String.join(", ", value));
            });
            
            // Build the response
            ApiTestResponse apiTestResponse = ApiTestResponse.builder()
                    .statusCode(response.getStatusCodeValue())
//                    .statusText(response.getStatusCode().getReasonPhrase())
                    .headers(responseHeaders)
                    .body(response.getBody())
                    .responseTime(responseTimeMs + " ms")
                    .size((long) (response.getBody() != null ? response.getBody().length() : 0))
                    .contentType(response.getHeaders().getContentType() != null ? 
                            response.getHeaders().getContentType().toString() : "")
                    .build();
            
            // Save the test to history
            saveApiTest(request, apiTestResponse);
            
            return apiTestResponse;
            
        } catch (Exception e) {
            log.error("Error testing API", e);
            
            ApiTestResponse errorResponse = ApiTestResponse.builder()
                    .error(e.getMessage())
                    .build();
            
            // Save the failed test to history
            saveApiTest(request, errorResponse);
            
            return errorResponse;
        }
    }

    @Override
    public String saveApiTest(ApiTestRequest request, ApiTestResponse response) {
        try {
            ApiTest apiTest = ApiTest.builder()
                    .userId(request.getUserId())
                    .method(request.getMethod())
                    .url(request.getUrl())
                    .contentType(request.getContentType())
                    .requestBody(request.getBody())
                    .requestHeaders(objectMapper.writeValueAsString(request.getHeaders()))
                    .timeout(request.getTimeout())
                    .followRedirects(request.getFollowRedirects())
                    .createdAt(TimestampUtils.now())
                    .statusCode(response.getStatusCode())
                    .statusText(response.getStatusText())
                    .responseHeaders(objectMapper.writeValueAsString(response.getHeaders()))
                    .responseBody(response.getBody())
                    .responseTime(response.getResponseTime())
                    .responseSize(response.getSize())
                    .responseContentType(response.getContentType())
                    .error(response.getError())
                    .favorite(false)
                    .build();
            
            apiTest = apiTestRepository.save(apiTest);
            
            return apiTest.getId().toString();
        } catch (Exception e) {
            log.error("Error saving API test", e);
            return null;
        }
    }

    @Override
    public ApiTestResponse[] getApiTestHistory(String userId, int limit) {
        List<ApiTest> history = apiTestRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
        
        return history.stream()
                .map(this::mapToApiTestResponse)
                .toArray(ApiTestResponse[]::new);
    }
    
    private ApiTestResponse mapToApiTestResponse(ApiTest apiTest) {
        try {
            Map<String, String> headers = new HashMap<>();
            if (apiTest.getResponseHeaders() != null && !apiTest.getResponseHeaders().isEmpty()) {
                headers = objectMapper.readValue(apiTest.getResponseHeaders(), Map.class);
            }
            
            return ApiTestResponse.builder()
                    .statusCode(apiTest.getStatusCode())
                    .statusText(apiTest.getStatusText())
                    .headers(headers)
                    .body(apiTest.getResponseBody())
                    .responseTime(apiTest.getResponseTime())
                    .size(apiTest.getResponseSize())
                    .contentType(apiTest.getResponseContentType())
                    .error(apiTest.getError())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping API test to response", e);
            return ApiTestResponse.builder()
                    .error("Error mapping API test to response: " + e.getMessage())
                    .build();
        }
    }
    
    public List<ApiTest> getUserTests(String userId) {
        return apiTestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<ApiTest> getUserFavoriteTests(String userId) {
        return apiTestRepository.findByUserIdAndFavoriteIsTrueOrderByCreatedAtDesc(userId);
    }
    
    public ApiTest getTestById(Long testId) {
        return apiTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("API test not found with ID: " + testId));
    }
    
    public void toggleFavorite(Long testId) {
        ApiTest apiTest = getTestById(testId);
        apiTest.setFavorite(!apiTest.getFavorite());
        apiTestRepository.save(apiTest);
    }
    
    public void deleteTest(Long testId) {
        apiTestRepository.deleteById(testId);
    }
}
