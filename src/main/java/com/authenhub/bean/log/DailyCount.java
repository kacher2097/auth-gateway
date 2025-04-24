package com.authenhub.bean.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCount {
    private String id; // Date in format YYYY-MM-DD
    private Long count;
}
