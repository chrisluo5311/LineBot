package com.infotran.springboot.webcrawler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:webcrawl.properties",
        "classpath:application.properties"
})
public class CrawConfig {

}
