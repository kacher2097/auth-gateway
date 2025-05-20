package com.authenhub.bean.passwordmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportPasswordsResponse {
    private int totalImported;
    private int totalSkipped;
    private int totalFailed;
    private String message;
}
