package com.infotran.springboot.welcome.init;

import com.infotran.springboot.MedicineStore.Controller.GetMaskJsonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.infotran.springboot.ConfirmCase.controller.CrawlCovidNumbers;

@Component
@Order(value=2)
public class InitRunner implements CommandLineRunner {
	
	@Autowired
	CrawlCovidNumbers crawl;

	@Autowired
	GetMaskJsonController getMaskJson;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Successful!");
		getMaskJson.run();
	}

}
