package com.authenhub.bean.tool.codegenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeGeneratorRequest {
    private String userId;
    private String codeType; // crud, component, api
    private String modelName;
    private String framework;
    private String database;
    private String fields;
    private String componentName;
    private String componentType; // functional, class
    private List<String> features; // crud, pagination, search, filter, sort
    private String apiName;
    private Boolean authRequired;
    private String language; // javascript, typescript, java, python, php
}
