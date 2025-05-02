package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.apitester.ApiTestRequest;
import com.authenhub.bean.tool.apitester.ApiTestResponse;

public interface IApiTesterService {
    /**
     * Test an API endpoint
     * @param request The API test request
     * @return The API test response
     */
    ApiTestResponse testApi(ApiTestRequest request);
    
    /**
     * Save an API test request to history
     * @param request The API test request
     * @param response The API test response
     * @return The saved request ID
     */
    String saveApiTest(ApiTestRequest request, ApiTestResponse response);
    
    /**
     * Get API test history
     * @param userId The user ID
     * @param limit The maximum number of history items to return
     * @return The API test history
     */
    ApiTestResponse[] getApiTestHistory(String userId, int limit);
}
