package com.infotran.springboot;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class LineBotApplicationTests {

	@Autowired
	MenuIdService menuIdService;

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
	void contextLoads() {

	}

}
