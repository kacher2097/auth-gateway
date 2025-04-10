package com.authenhub.filter;

import com.authenhub.service.AccessLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AccessLogFilter extends OncePerRequestFilter {

    private final AccessLogService accessLogService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        
        // Create a wrapper to capture the status code
        StatusCapturingResponseWrapper responseWrapper = new StatusCapturingResponseWrapper(response);
        
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            // Log the request after it's processed
            accessLogService.logAccess(request, responseWrapper.getStatus(), startTime);
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip logging for static resources
        return path.contains("/static/") || 
               path.contains("/favicon.ico") ||
               path.contains("/actuator/");
    }
    
    // Custom response wrapper to capture status code
    private static class StatusCapturingResponseWrapper extends jakarta.servlet.http.HttpServletResponseWrapper {
        private int status = 200; // Default status

        public StatusCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
            super.setStatus(status);
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.status = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.status = sc;
            super.sendError(sc, msg);
        }

        public int getStatus() {
            return status;
        }
    }
}
