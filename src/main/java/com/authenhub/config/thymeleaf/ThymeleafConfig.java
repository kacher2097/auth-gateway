package com.authenhub.config.thymeleaf;

import lombok.Data;

@Data

public class ThymeleafConfig {
    private boolean checkTemplate;
    private boolean checkTemplateLocation;
    private boolean enabled;
    private String prefix;
    private String suffix;
}
