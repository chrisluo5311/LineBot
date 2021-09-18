package com.infotran.springboot;

import com.infotran.springboot.annotation.LogInfo;
import com.infotran.springboot.confirmcase.controller.CrawlCovidNumbers;
import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.infotran.springboot.medicinestore.controller.GetMaskJsonController;
import com.infotran.springboot.medicinestore.model.MedicineStore;
import com.infotran.springboot.medicinestore.service.MedicineStoreService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class LineBotApplicationTests extends BaseLineBotTest{

	@Resource
	ConfirmCaseService caseService;

	@Autowired
	MedicineStore medicineStore;

	@Resource
	MedicineStoreService medicineStoreService;

	@Test
	void test01() {
//		this.execute("hi");
		System.out.println("hi");
	}


}
