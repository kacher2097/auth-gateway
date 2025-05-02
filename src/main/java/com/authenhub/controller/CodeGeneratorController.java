package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.codegenerator.CodeGeneratorRequest;
import com.authenhub.bean.tool.codegenerator.CodeGeneratorResponse;
import com.authenhub.service.interfaces.ICodeGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/code-generator")
@RequiredArgsConstructor
public class CodeGeneratorController {

    private final ICodeGeneratorService codeGeneratorService;

    /**
     * Generate code based on the request
     * @param request The code generator request
     * @return The code generator response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CodeGeneratorResponse>> generateCode(@RequestBody CodeGeneratorRequest request) {
        CodeGeneratorResponse response = codeGeneratorService.generateCode(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Export generated code to a file
     * @param response The code generator response
     * @return The file content
     */
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportGeneratedCode(@RequestBody CodeGeneratorResponse response) {
        byte[] fileContent = codeGeneratorService.exportGeneratedCode(response);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(response.getLanguage()));
        headers.setContentDispositionFormData("attachment", response.getFileName());
        
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    
    private MediaType getMediaType(String language) {
        switch (language.toLowerCase()) {
            case "javascript":
                return MediaType.parseMediaType("application/javascript");
            case "typescript":
                return MediaType.parseMediaType("application/typescript");
            case "java":
                return MediaType.parseMediaType("text/x-java-source");
            case "python":
                return MediaType.parseMediaType("text/x-python");
            case "php":
                return MediaType.parseMediaType("application/x-php");
            default:
                return MediaType.TEXT_PLAIN;
        }
    }
}
