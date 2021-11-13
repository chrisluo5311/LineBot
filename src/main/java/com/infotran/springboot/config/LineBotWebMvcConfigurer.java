package com.infotran.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chris
 */
@Configuration
@Slf4j
public class LineBotWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置靜態資源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        // "file:/D:/IdeaProject/LineBot/src/main/resources/static/"
    }
}
