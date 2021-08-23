package com.infotran.springboot;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.ConfirmCase.service.ConfirmCaseService;
import com.infotran.springboot.LineBot.Model.MenuID;
import com.infotran.springboot.LineBot.service.MenuIdService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class LineBotApplicationTests {

	@Autowired
	MenuIdService menuIdService;
	
	@Autowired
	ConfirmCaseService caseService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	RedisTemplate<Object, MenuID> menuIDRedisTemplate;

	@Test
	public void test01(){
//		stringRedisTemplate.opsForValue().append("msg","hello world");
//		String msg = stringRedisTemplate.opsForValue().get("msg");
//		System.out.println(msg);
//		stringRedisTemplate.opsForList().leftPush("mylist","1");
//		stringRedisTemplate.opsForList().leftPush("mylist","2");
	}

	@Test
	public void test02(){
		MenuID menu = menuIdService.getMenuByName("covidMenu");
//		redisTemplate.opsForValue().set("menu-01",menu);
		menuIDRedisTemplate.opsForValue().set("menu-01",menu);
	}



	@Test
	void test03() {
		System.out.print(LocalDate.now().minusDays(1));
		ConfirmCase confirmCase = caseService.findByConfirmTime(LocalDate.now().minusDays(1));
		System.out.print(confirmCase.getTodayAmount());
		String message = "";
		message = "指揮中心快訊：新增"+ String.valueOf(confirmCase.getTodayAmount()) + "例COVID-19確定病例，校正回歸數" + String.valueOf(confirmCase.getReturnAmount())+ "例，死亡人數" + String.valueOf(confirmCase.getDeathAmount()) + "例。$";
		System.out.print(message);
	}


}
