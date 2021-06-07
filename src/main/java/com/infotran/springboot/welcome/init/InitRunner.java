package com.infotran.springboot.welcome.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.infotran.springboot.ConfirmCase.controller.crawlCovidNumbers;

@Component
@Order(value=2)
public class InitRunner implements CommandLineRunner {
	
	@Autowired
	crawlCovidNumbers crawl;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Successful!");
		crawl.run();
	}

}
