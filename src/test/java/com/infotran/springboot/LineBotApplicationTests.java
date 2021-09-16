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
class LineBotApplicationTests {

	@Resource
	ConfirmCaseService caseService;

	@Autowired
	MedicineStore medicineStore;

	@Resource
	MedicineStoreService medicineStoreService;

	public String CSV_MOCK = "醫事機構代碼,醫事機構名稱,醫事機構地址,醫事機構電話,成人口罩剩餘數,兒童口罩剩餘數,來源資料時間\n" +
			"0145080011,衛生福利部花蓮醫院豐濱原住民分院,花蓮縣豐濱鄉豐濱村光豐路４１號,8358141,1560,1090,2021/09/16 21:14:29\n" +
			"0291010010,連江縣立醫院,連江縣南竿鄉復興村２１７號,623995,8000,1150,2021/09/16 21:14:29";

	@Test
	void test01() throws IOException {
//		Arrays.asList(CSV_MOCK.split(",")).stream().map(l -> l.split(" ")).forEach(System.out::println);
		List<String> list = new ArrayList<>();
//		Arrays.asList( CSV_MOCK.split("\n")).stream().skip(1).forEach(list::add);
//		for (int i = 0;i< list.size();i++){
//			String[] args = list.get(i).split(",");
//
//		}
		Arrays.asList( CSV_MOCK.split(",")).stream().skip(7).forEach(System.out::println);


//		for (int i = 0;i< list.size();i++){
//			Integer flag = i%7;
//			switch (flag){
//				case 0:
//					medicineStore.setId(list.get(i));
//					break;
//				case 1:
//					medicineStore.setName(list.get(i));
//					break;
//				case 2:
//					medicineStore.setAddress(list.get(i));
//					break;
//				case 3:
//					medicineStore.setPhoneNumber(list.get(i));
//					break;
//				case 4:
//					medicineStore.setMaskAdult(Integer.valueOf(list.get(i)));
//					break;
//				case 5:
//					medicineStore.setMaskChild(Integer.valueOf(list.get(i)));
//					break;
//				case 6:
//					medicineStore.setUpdateTime(list.get(i));
////					medicineStoreService.save(medicineStore);
//					System.out.println(medicineStore.toString());
//					break;
//
//			}
//		}
	}


}
