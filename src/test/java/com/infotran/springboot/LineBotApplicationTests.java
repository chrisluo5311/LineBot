package com.infotran.springboot;

import com.infotran.springboot.annotation.LogInfo;
import com.infotran.springboot.confirmcase.controller.CrawlCovidNumbers;
import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.infotran.springboot.medicinestore.controller.GetMaskJsonController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class LineBotApplicationTests {

	@Resource
	ConfirmCaseService caseService;

	@Test
	void test01() throws IOException {
	}

}
