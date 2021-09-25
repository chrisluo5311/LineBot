package com.infotran.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LineBotApplicationTests {

//
//	@Override
//	public String execute(String arg){
//		String param = super.execute("gg");
//		System.out.println(param);
//		return param;
//	}
	@Test
	public void getClassName() throws ClassNotFoundException {
		Class clz = Class.forName("com.infotran.springboot.linebot.service.messagehandler.HandleTodayAmountMessage");
		System.out.println(clz.getSimpleName());
		System.out.println("hi");
	}


}
