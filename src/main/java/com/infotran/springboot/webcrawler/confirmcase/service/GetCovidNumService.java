package com.infotran.springboot.webcrawler.confirmcase.service;

import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.SSLHelper;
import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class GetCovidNumService implements ClientUtil {

	private static final String LOG_PREFIX = "CrawlCovidNumbers";

	@Value("${CDC.URL}")
	public static String CDC_URL;// 新聞首頁

	private final String titleName = "指揮中心公布新增";
	
	private final String DOMESTIC_NEWNUM = "國內新增";
	
	private final String RETURN_NUM = "校正回歸本土個案";
	
	private final String DEATH_NUM = "確診個案中新增";


	@Autowired
	private ConfirmCaseService cService;

	@Autowired
	RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate;

	/**
	 * Parse first CDC URL
	 * Get detailed news URL
	 * @param body 疾管局新闻首页的连结
	 *
	 */
	public String getURLOfNewsDetail(String body) {
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

	/**
	 * Parse the detailed news body
	 * Save the confirmed numbers
	 * @param detailedURL 当日新增确诊数目的新闻连结
	 *
	 */
	public ConfirmCase parseBody(String detailedURL) {
		ConfirmCase confirmCase = null;
		try {
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
//			Document doc = Jsoup.connect(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divchildren = doc.select("div.news-v3-in > div ");
			String divchild = divchildren.text();
			//今日確診數
			Integer newNum = getNumFromDivchild(divchild,DOMESTIC_NEWNUM);
			//校正回歸數
			Integer reNum = getNumFromDivchild(divchild,RETURN_NUM);
			//總數
			Integer totalNum = newNum + reNum;
			//死亡人數
			Integer deathNum = getNumFromDivchild(divchild,DEATH_NUM);
			log.info("新增數目:{},校正回歸:{},總數:{},死亡數目:{}",newNum,reNum,totalNum,deathNum);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .newsUrl(detailedURL)
										 .build();
			log.info("{} 今日確診物件: {}",LOG_PREFIX,cfc);
			if(confirmCaseRedisTemplate.hasKey("今日確診")){
				confirmCaseRedisTemplate.delete("今日確診");
			}
			confirmCaseRedisTemplate.opsForValue().set("今日確診",cfc);
			confirmCase= cService.save(cfc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return confirmCase;
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
