package com.authenhub.bean.proxy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private int totalProcessed;
    private int successCount;
    private int failCount;
    private List<ImportError> errors;
    private List<ProxyResponse> importedProxies;
}
