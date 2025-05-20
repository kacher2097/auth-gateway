package com.authenhub.bean.passwordmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PwManagerSearchReq {
    private String keyword;
    private String provider;
    private String username;
    private String email;
}
