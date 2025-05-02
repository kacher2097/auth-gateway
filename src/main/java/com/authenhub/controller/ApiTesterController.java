package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.apitester.ApiTestRequest;
import com.authenhub.bean.tool.apitester.ApiTestResponse;
import com.authenhub.service.interfaces.IApiTesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/api-tester")
@RequiredArgsConstructor
public class ApiTesterController {

    private final IApiTesterService apiTesterService;

    /**
     * Test an API endpoint
     * @param request The API test request
     * @return The API test response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ApiTestResponse>> testApi(@RequestBody ApiTestRequest request) {
        ApiTestResponse response = apiTesterService.testApi(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Save an API test request to history
     * @param request The API test request
     * @param response The API test response
     * @return The saved request ID
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveApiTest(
            @RequestBody ApiTestRequest request,
            @RequestParam ApiTestResponse response) {
        
        String id = apiTesterService.saveApiTest(request, response);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    /**
     * Get API test history
     * @param userId The user ID
     * @param limit The maximum number of history items to return
     * @return The API test history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<ApiTestResponse[]>> getApiTestHistory(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        ApiTestResponse[] history = apiTesterService.getApiTestHistory(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
