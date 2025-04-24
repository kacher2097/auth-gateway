package com.authenhub.bean.facebook.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookUserResponse {
    private String id;
    private String name;
    private String email;
}
