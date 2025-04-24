package com.authenhub.bean.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrowserCount {
    private String id; // Browser name
    private Long count;
}
