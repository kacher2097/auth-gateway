package com.authenhub.utils;

import com.authenhub.constant.Constant;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class Utils {

    private Utils() {}

    public static String getRequestUri(ServletRequest servletRequest) {
        return ((HttpServletRequest) servletRequest).getRequestURI();
    }

    public static long end(long start) {
        return System.currentTimeMillis() - start;
    }

    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (Strings.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (Strings.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (Strings.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (Constant.LOCALHOST_IPV4.equals(ipAddress) || Constant.LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    log.debug("getClientIp has ex: ", e);
                }
            }
        }

        if (Strings.isNotBlank(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }
}
