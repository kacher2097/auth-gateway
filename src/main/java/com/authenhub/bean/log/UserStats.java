package com.authenhub.bean.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {
    private String id; // User ID
    private String username;
    private Long count;
}
