package com.authenhub.bean.tool.codegenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGeneratorResponse {
    private String code;
    private String language;
    private String fileName;
    private String generatedAt;
    private String codeType;
    private String framework;
}
