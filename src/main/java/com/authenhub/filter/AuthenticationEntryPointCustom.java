//package com.authenhub.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//@Slf4j
//@Component
//@AllArgsConstructor
//public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {
//
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
//        throws IOException {
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setHeader("Content-Type", "application/json");
//        PrintWriter writer = response.getWriter();
//        writer.println(objectMapper.writeValueAsString(BaseResponse.of(BaseResponseCode.FORBIDDEN)));
//        writer.close();
//    }
//}
