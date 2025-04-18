package com.authenhub.filter;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.constant.enums.ApiResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json");
        PrintWriter writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(ApiResponse.error(ApiResponseCode.FORBIDDEN)));
        writer.close();
    }
}
