package com.authenhub.facebook.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
@Profile("dev") // Chỉ áp dụng cho môi trường phát triển
public class SSLConfig {

    @PostConstruct
    public void disableSslValidation() throws NoSuchAlgorithmException, KeyManagementException {
        // Tạo một TrustManager chấp nhận tất cả chứng chỉ
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Tạo một HostnameVerifier chấp nhận tất cả hostname
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Cài đặt TrustManager và HostnameVerifier
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
