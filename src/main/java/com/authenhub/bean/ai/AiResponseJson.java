package com.authenhub.bean.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseJson {

    private String title;
    private String content;
    private String excerpt;
}
