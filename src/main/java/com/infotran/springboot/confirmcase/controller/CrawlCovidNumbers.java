package com.infotran.springboot.confirmcase.controller;

import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.SSLHelper;
import com.infotran.springboot.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Controller
@Slf4j
public class CrawlCovidNumbers implements ClientUtil, CommandLineRunner {

	private final String CDC_URL = "http://at.cdc.tw/YEc68Q";// 新聞首頁

	private String CDC_NewsDetail = "";// 進入新聞

	private final String titleName = "指揮中心公布新增";
	
	private final String DOMESTIC_NEWNUM = "國內新增";
	
	private final String RETURN_NUM = "校正回歸本土個案";
	
	private final String DEATH_NUM = "確診個案中新增";

	private static final String LOG_PREFIX = "CrawlCovidNumbers";

	@Autowired
	private ConfirmCaseService cService;

	@Autowired
	RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate;

	@Override
	public void run(String... args) throws Exception {
		executeCrawlCovid();
	}

	/*
	 * 執行爬蟲
	 */
	@Scheduled(cron = "0 0/5 14 * * ?")
	public void executeCrawlCovid() throws IOException {
		ConfirmCase confirmCase = cService.findByConfirmTime(LocalDate.now());
		if (confirmCase!=null) return;
		Request request = new Request.Builder().url(CDC_URL).get().build(); // get post put 等
		Call call = client.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				log.info("@@@@@@ {} 爬蟲成功 @@@@@@",LOG_PREFIX);
				String body = response.body().string();//整頁內容
				CDC_NewsDetail = getURLOfNewsDetail(body);
				parseBody(CDC_NewsDetail);
			}
		});
	}

	/*
	 * Parse first CDC URL
	 * Get detailed news URL
	 */
	private String getURLOfNewsDetail(String body) {
		StringBuilder res = new StringBuilder("https://www.cdc.gov.tw");
		Document doc = Jsoup.parse(body);
		Elements newslists = doc.select(".cbp-item");
		Map<String, String> todayMap = TimeUtil.genTodayDate();
		for (Element element : newslists) {
			String month = element.select("p.icon-year").text().substring(7,8);
			String date = element.select("p.icon-date").text();
			String tname = element.select(".content-boxes-v3 > a").attr("title");
			if (todayMap.containsKey(month) && todayMap.containsValue(date) && tname.indexOf("確定病例") != -1) {
				log.info("[The rest of the news url is here : {} ]" , element.select(".content-boxes-v3 > a").attr("href"));
				res.append(element.select(".content-boxes-v3 > a").attr("href"));
				return res.toString();
			}
		}
		log.info("News detailed url: {} "+res.toString());
		return res.toString();
	}

	/*
	 * Parse the detailed news body
	 * Save the confirmed numbers
	 */
	private void parseBody(String detailedURL) {
		try {
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
//			Document doc = Jsoup.connect(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divchildren = doc.select("div.news-v3-in > div ");
			String divchild = divchildren.text();
			Integer numeric_Start = 0;//數字起點
			//今日確診數
			Integer newNum = getNumFromDivchild(divchild,DOMESTIC_NEWNUM);
			Integer reNum = getNumFromDivchild(divchild,RETURN_NUM);
			Integer totalNum = newNum + reNum;
			Integer deathNum = getNumFromDivchild(divchild,DEATH_NUM);
			log.info("新增數目{},校正回歸{},總數{},死亡數目{}",newNum,reNum,totalNum,deathNum);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .build();
			log.info("{} 今日確診物件 {}",LOG_PREFIX,cfc);
			confirmCaseRedisTemplate.opsForValue().set("今日確診",cfc);
			cService.save(cfc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根據關鍵字取得不同的確診數目
	 * @param divchild 新聞body
	 * @param keyword 搜索的關鍵字
	 * @return Integer 確診數目
	 * */
	private Integer getNumFromDivchild(String divchild,String keyword) {
		Integer numeric_Start = 0;//數字起點
		Integer target = 0;//返回結果
		if (divchild.indexOf(keyword) != -1) {
			numeric_Start = divchild.indexOf(keyword)+ keyword.length();// 數字起點
			target = getNumberFromEachCategory(numeric_Start,divchild);
		}
		return target;
	}

	private int getNumberFromEachCategory(int index, String article) {
		Integer sum = 0;
		while (Character.isDigit(article.charAt(index))) {
			sum = sum*10 + Character.getNumericValue(article.charAt(index));
			index++;
		}
		return sum;
	}

}
