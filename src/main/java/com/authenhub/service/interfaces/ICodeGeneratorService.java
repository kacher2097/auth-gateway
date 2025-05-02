package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.codegenerator.CodeGeneratorRequest;
import com.authenhub.bean.tool.codegenerator.CodeGeneratorResponse;

public interface ICodeGeneratorService {
    /**
     * Generate code based on the request
     * @param request The code generator request
     * @return The code generator response containing the generated code
     */
    CodeGeneratorResponse generateCode(CodeGeneratorRequest request);
    
    /**
     * Save generated code to a file
     * @param response The code generator response
     * @return The file content as byte array
     */
    byte[] exportGeneratedCode(CodeGeneratorResponse response);
}
