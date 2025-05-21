package com.authenhub.config.fb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@Profile("dev") // Chỉ áp dụng cho môi trường phát triển
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            log.debug("============================= REQUEST BEGIN =============================");
            log.debug("URI: {}", request.getURI());
            log.debug("Method: {}", request.getMethod());
            log.debug("Headers: {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            log.debug("============================= REQUEST END ===============================");
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("============================= RESPONSE BEGIN =============================");
            log.debug("Status code: {}", response.getStatusCode());
            log.debug("Headers: {}", response.getHeaders());
            
            String responseBody = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
            if (responseBody.length() > 1000) {
                log.debug("Response body (truncated): {}", responseBody.substring(0, 1000) + "...");
            } else {
                log.debug("Response body: {}", responseBody);
            }
            
            log.debug("============================= RESPONSE END ===============================");
        }
    }
}
