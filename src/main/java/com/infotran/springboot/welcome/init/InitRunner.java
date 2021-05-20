package com.infotran.springboot.welcome.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=2)
public class InitRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Successful!");
	}

}
