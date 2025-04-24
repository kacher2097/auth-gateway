package com.authenhub.service.client;

import com.authenhub.config.fb.FacebookApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookApiClient {
    private final RestTemplate restTemplate;
    private final FacebookApiProperties properties;
    
    /**
     * Gửi request GET đến Facebook Graph API
     * @param endpoint Endpoint API
     * @param accessToken Token truy cập
     * @param responseType Kiểu dữ liệu trả về
     * @param params Tham số bổ sung
     * @return Kết quả từ API
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <T> T get(String endpoint, String accessToken, Class<T> responseType, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getApiBaseUrl() + endpoint);
        
        if (accessToken != null) {
            builder.queryParam("access_token", accessToken);
        }
        
        if (params != null) {
            params.forEach(builder::queryParam);
        }
        
        String url = builder.toUriString();
        log.debug("Sending GET request to: {}", url);
        
        return restTemplate.getForObject(url, responseType);
    }
    
    /**
     * Gửi request POST đến Facebook Graph API
     * @param endpoint Endpoint API
     * @param accessToken Token truy cập
     * @param body Dữ liệu gửi đi
     * @param responseType Kiểu dữ liệu trả về
     * @return Kết quả từ API
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <T> T post(String endpoint, String accessToken, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getApiBaseUrl() + endpoint)
            .queryParam("access_token", accessToken);
        
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        String url = builder.toUriString();
        
        log.debug("Sending POST request to: {}", url);
        
        return restTemplate.postForObject(url, entity, responseType);
    }
    
    /**
     * Gửi request DELETE đến Facebook Graph API
     * @param endpoint Endpoint API
     * @param accessToken Token truy cập
     * @return true nếu xóa thành công
     */
    @Retryable(value = RestClientException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public boolean delete(String endpoint, String accessToken) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(properties.getApiBaseUrl() + endpoint)
            .queryParam("access_token", accessToken);
        
        String url = builder.toUriString();
        log.debug("Sending DELETE request to: {}", url);
        
        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Error deleting resource: {}", e.getMessage());
            return false;
        }
    }
}
