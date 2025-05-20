package com.authenhub.bean.passwordmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportPasswordsResponse {
    private List<PasswordExportItem> passwords;
    private String format;
    private String exportedData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordExportItem {
        private Long id;
        private String siteUrl;
        private String username;
        private String password;
        private String iconUrl;
    }
}
