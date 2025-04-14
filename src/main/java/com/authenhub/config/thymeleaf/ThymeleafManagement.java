package com.authenhub.config.thymeleaf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ThymeleafManagement implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // this won't add a 2nd MappingJackson2HttpMessageConverter
        // as the SOLUTION 2 is doing but also might seem complicated
        converters.stream().filter(c -> c instanceof MappingJackson2HttpMessageConverter).forEach(c -> {
            // check default included objectMapper._registeredModuleTypes,
            // e.g. Jdk8Module, JavaTimeModule when creating the ObjectMapper
            // without Jackson2ObjectMapperBuilder
            ((MappingJackson2HttpMessageConverter) c).setObjectMapper(this.objectMapper);
        });
    }

    @Bean
    public SpringResourceTemplateResolver secondaryTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("file:./config/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

}
