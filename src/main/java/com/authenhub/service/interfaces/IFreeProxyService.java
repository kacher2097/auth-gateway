package com.authenhub.service.interfaces;

import com.authenhub.bean.proxy.CheckResult;
import com.authenhub.bean.proxy.ImportResult;
import com.authenhub.bean.proxy.ProxyRequest;
import com.authenhub.bean.proxy.ProxyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for free proxy service operations
 */
public interface IFreeProxyService {

    /**
     * Get all proxies
     *
     * @return list of proxies
     */
    List<ProxyResponse> getAllProxies();

    /**
     * Get active proxies
     *
     * @return list of active proxies
     */
    List<ProxyResponse> getActiveProxies();

    /**
     * Get proxies by protocol
     *
     * @param protocol proxy protocol
     * @return list of proxies
     */
    List<ProxyResponse> getProxiesByProtocol(String protocol);

    /**
     * Get proxies by country
     *
     * @param country proxy country
     * @return list of proxies
     */
    List<ProxyResponse> getProxiesByCountry(String country);

    /**
     * Get fast proxies
     *
     * @param maxResponseTime maximum response time
     * @return list of proxies
     */
    List<ProxyResponse> getFastProxies(int maxResponseTime);

    /**
     * Get reliable proxies
     *
     * @param minUptime minimum uptime
     * @return list of proxies
     */
    List<ProxyResponse> getReliableProxies(double minUptime);

    /**
     * Create a new proxy
     *
     * @param request proxy request
     * @return created proxy
     */
    ProxyResponse createProxy(ProxyRequest request, String token);

    /**
     * Update a proxy
     *
     * @param id proxy id
     * @param request proxy request
     * @return updated proxy
     */
    ProxyResponse updateProxy(String id, ProxyRequest request);

    /**
     * Delete a proxy
     *
     * @param id proxy id
     */
    void deleteProxy(String id);

    /**
     * Check proxy by id
     *
     * @param id proxy id
     * @return check result
     */
    CheckResult checkProxyById(String id);

    /**
     * Import proxies from file
     *
     * @param file proxy file
     * @param fileType file type
     * @return import result
     */
    ImportResult importProxies(MultipartFile file, String fileType);

    ProxyResponse getProxyById(String id);
}
