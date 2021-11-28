package com.infotran.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
	public void demo() {
		String a = "hello";
		String b = "world";
		get(a,b);
	}

	public void get(String...val){
		System.out.println(val);
	}


}
