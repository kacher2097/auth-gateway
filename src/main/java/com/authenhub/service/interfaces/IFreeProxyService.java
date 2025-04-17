package com.authenhub.service.interfaces;

import com.authenhub.dto.FreeProxyDto;
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
    List<FreeProxyDto.Response> getAllProxies();
    
    /**
     * Get active proxies
     *
     * @return list of active proxies
     */
    List<FreeProxyDto.Response> getActiveProxies();
    
    /**
     * Get proxies by protocol
     *
     * @param protocol proxy protocol
     * @return list of proxies
     */
    List<FreeProxyDto.Response> getProxiesByProtocol(String protocol);
    
    /**
     * Get proxies by country
     *
     * @param country proxy country
     * @return list of proxies
     */
    List<FreeProxyDto.Response> getProxiesByCountry(String country);
    
    /**
     * Get fast proxies
     *
     * @param maxResponseTime maximum response time
     * @return list of proxies
     */
    List<FreeProxyDto.Response> getFastProxies(int maxResponseTime);
    
    /**
     * Get reliable proxies
     *
     * @param minUptime minimum uptime
     * @return list of proxies
     */
    List<FreeProxyDto.Response> getReliableProxies(double minUptime);
    
    /**
     * Create a new proxy
     *
     * @param request proxy request
     * @return created proxy
     */
    FreeProxyDto.Response createProxy(FreeProxyDto.Request request, String token);
    
    /**
     * Update a proxy
     *
     * @param id proxy id
     * @param request proxy request
     * @return updated proxy
     */
    FreeProxyDto.Response updateProxy(String id, FreeProxyDto.Request request);
    
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
    FreeProxyDto.CheckResult checkProxyById(String id);
    
    /**
     * Import proxies from file
     *
     * @param file proxy file
     * @param fileType file type
     * @return import result
     */
    FreeProxyDto.ImportResult importProxies(MultipartFile file, String fileType);

    FreeProxyDto.Response getProxyById(String id);
}
