package com.authenhub.config.filter;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;


@Slf4j
@Configuration
public class LogFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {

        try {
            long startTime = System.currentTimeMillis();
            MDC.put("token", NanoIdUtils.randomNanoId());
            filterChain.doFilter(servletRequest, servletResponse);
            long processTime = System.currentTimeMillis() - startTime;
            String uri = ((jakarta.servlet.http.HttpServletRequest) servletRequest).getRequestURI();
            String ipClient = ((jakarta.servlet.http.HttpServletRequest) servletRequest).getRemoteAddr();
            log.info("Request to [{}] from IP [{}] finish in {}ms", uri, ipClient, processTime);
            MDC.clear();
        } catch (ClientAbortException e) {
            log.warn("Broken pipe ", e);
        } catch (Exception e) {
            log.error("Error with request : {} :", servletRequest, e);
        }
    }
}
