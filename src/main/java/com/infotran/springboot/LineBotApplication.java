package com.infotran.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.infotran.springboot.LineBot.controller.createRichMenu;
import com.infotran.springboot.LineBot.controller.deleteRichMenu;

@SpringBootApplication
@EnableJpaAuditing
public class LineBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(LineBotApplication.class, args);
	}

}
