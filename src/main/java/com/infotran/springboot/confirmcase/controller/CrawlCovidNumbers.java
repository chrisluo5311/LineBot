package com.infotran.springboot.confirmcase.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.infotran.springboot.Util.ClientUtil;
import com.infotran.springboot.Util.TimeUtil;
import com.infotran.springboot.Util.SSLHelper;
import com.infotran.springboot.annotation.LogInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.infotran.springboot.confirmcase.model.ConfirmCase;
import com.infotran.springboot.confirmcase.service.ConfirmCaseService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

@Controller
@Slf4j
public class CrawlCovidNumbers implements ClientUtil {

	private final String CDC_URL = "http://at.cdc.tw/YEc68Q";// 新聞首頁

	private String CDC_NewsDetail = "";// 進入新聞

	private final String titleName = "指揮中心公布新增";
	
	private final String DOMESTIC_NEWNUM = "國內新增";
	
	private final String RETURN_NUM = "校正回歸本土個案";
	
	private final String DEATH_NUM = "確診個案中新增";

	@Autowired
	private ConfirmCaseService cService;

	/*
	 * 執行爬蟲
	 */
//	@Scheduled(cron = "0 0/5 14 * * ?")
	public void run() throws IOException {
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
				String body = response.body().string();
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
				log.info("[The rest of the news url is here] {} " , element.select(".content-boxes-v3 > a").attr("href"));
				res.append(element.select(".content-boxes-v3 > a").attr("href"));
				return res.toString();
			}
		}
		log.info("this is newsdetail url ==> "+res.toString());
		return res.toString();
	}

	/*
	 * Parse the detailed news body
	 * Save the confirmed numbers
	 */
	private void parseBody(String detailedURL) {
		try {
			log.info("[CrawlCovidNumbers.parseBody() arg is {}]",detailedURL);
			log.info("[CrawlCovidNumbers.parseBody() method starts here]");
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
//			Document doc = Jsoup.connect(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divchildren = doc.select("div.news-v3-in > div ");
			String divchild = divchildren.text();
			Integer numeric_Start = 0;//數字起點
			Integer newNum = 0;
			if (divchild.indexOf(DOMESTIC_NEWNUM) != -1){
				numeric_Start = divchild.indexOf(DOMESTIC_NEWNUM)+ DOMESTIC_NEWNUM.length();// 數字起點
				newNum = getNumberFromEachCategory(numeric_Start,divchild);
			}
			Integer reNum = 0;
			if (divchild.indexOf(RETURN_NUM) != -1) {
				numeric_Start = divchild.indexOf(RETURN_NUM)+ RETURN_NUM.length();
				reNum = getNumberFromEachCategory(numeric_Start,divchild);
			}
			Integer totalNum = newNum + reNum;
			Integer deathNum = 0;
			if (divchild.indexOf(DEATH_NUM) != -1) {
				numeric_Start = divchild.indexOf(DEATH_NUM)+ DEATH_NUM.length();
				deathNum = getNumberFromEachCategory(numeric_Start,divchild);
			}
			log.info("新增數目{},校正回歸{},總數{},死亡數目{}",newNum,reNum,totalNum,deathNum);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .build();
			cService.save(cfc);
			log.info("Saving finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
