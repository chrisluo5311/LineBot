package com.infotran.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableRabbit
@ConfigurationPropertiesScan
public class LineBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(LineBotApplication.class, args);
	}

}
