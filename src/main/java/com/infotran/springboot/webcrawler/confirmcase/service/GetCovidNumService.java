package com.infotran.springboot.webcrawler.confirmcase.service;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.util.ClientUtil;
import com.infotran.springboot.util.SSLHelper;
import com.infotran.springboot.util.TimeUtil;
import com.infotran.springboot.webcrawler.confirmcase.model.ConfirmCase;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 針對 <br>
 * 1.當日新增數目 <br>
 * 2.確診案例中分佈 <br>
 * 3.校正回歸 <br>
 * 4.死亡數目 <br>
 * 進行爬蟲
 *
 * @author chris
 */
@Slf4j
@Service
public class GetCovidNumService implements ClientUtil {

	private static final String LOG_PREFIX = "CrawlCovidNumbers";

	/** confirm case redis key name */
	@Value("${CONFIRMCASE_REDIS_KEY}")
	private String CONFIRMCASE_REDIS_KEY;

	@Resource
	private ConfirmCaseService confirmCaseService;

	@Resource
	RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate;

	/**
	 * Parse first CDC URL <br>
	 * Get detailed news URL
	 * @param body 疾管局新闻首页的连结
	 * @throws LineBotException 新聞標題改變
	 */
	public void getUrlOfNewsDetail(String body) throws LineBotException {
		Document doc = Jsoup.parse(body);
		Elements newsLists = doc.select(".cbp-item");
		Map<String, String> todayMap = TimeUtil.genTodayDate();
		for (Element element : newsLists) {
			String month = element.select("p.icon-year").text().substring(7);
			String date = element.select("p.icon-date").text();
			String tName = element.select(".content-boxes-v3 > a").attr("title");
			if (todayMap.containsKey(month) && todayMap.containsValue(date) && tName.indexOf(properties.TITLE_NAME) != -1) {
				properties.PREFIX.append(element.select(".content-boxes-v3 > a").attr("href"));
				parseBody(properties.PREFIX.toString());
			}
		}
		log.error("{} 找不到新聞標題",LOG_PREFIX);
		throw new LineBotException(LineBotExceptionEnums.NEWS_TITLE_CHANGE);
	}

	/**
	 * Parse the detailed news body <br>
	 * Save the confirmed numbers
	 * @param detailedURL 当日新增确诊数目的新闻连结
	 * @throws LineBotException SSL Connection 失敗
	 */
	public void parseBody(String detailedURL) throws LineBotException {
		try {
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
			String divChild = doc.select("div.news-v3-in > div ").text();
			//今日確診數
			Integer newNum = getNumFromDivChild(properties.DOMESTIC_NEW_NUM,divChild);

			//校正回歸數
			Integer reNum = getNumFromDivChild(properties.RETURN_NUM,divChild);
			//判斷是否為全境外
			String domesticOrImportedCase = null;
			//ex. 國內新增6例
			String newNumKeyWord = properties.DOMESTIC_NEW_NUM.concat(String.valueOf(newNum)).concat("例");
			if(isAllImported(newNumKeyWord,";",divChild)){
				//全為境外
				domesticOrImportedCase = properties.ALL_IMPORTED;
			} else {
				//確診案例中分佈(有境外移入&本土)
				domesticOrImportedCase = getDomesticOrImportedCase("，","；",divChild);
			}
			//總數
			Integer totalNum = newNum + reNum;
			//死亡人數
			Integer deathNum = getNumFromDivChild(properties.DEATH_NUM,divChild);
			log.info("當日新增數目:{},確診案例中分佈:{},校正回歸:{},總數:{},死亡數目:{}",newNum,domesticOrImportedCase,reNum,totalNum,deathNum);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .domesticOrImportedCaseMemo(domesticOrImportedCase)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .newsUrl(detailedURL)
										 .build();
			if(Stream.of(newNum,reNum,deathNum).allMatch(x->x==0)){
				log.warn("當日確診人數及死亡人數指標皆為0，請手動確認");
			}
			confirmCaseRedisTemplate.delete(CONFIRMCASE_REDIS_KEY);
			confirmCaseRedisTemplate.opsForValue().set(CONFIRMCASE_REDIS_KEY,cfc);
			ConfirmCase confirmCase = confirmCaseService.save(cfc);
			if(Objects.isNull(confirmCase)){
				log.error("confirmCase 爬蟲成功 但新增至db失敗");
			}
		} catch (IOException e) {
			throw new LineBotException(LineBotExceptionEnums.FAIL_ON_SSLHELPER_CONNECTION,e.getMessage());
		}
	}

	/**
	 * 根據關鍵字取得不同的確診數目
	 * @param keyword 搜索的關鍵字
	 * @return Integer 確診數目
	 * */
	private Integer getNumFromDivChild(String keyword,String divChild) {
		//數字起點
		Integer numeric_Start = 0;
		//返回結果
		Integer target = 0;
		if (divChild.indexOf(keyword) != -1) {
			// 數字起點
			numeric_Start = divChild.indexOf(keyword)+ keyword.length();
			target = getNumberFromEachCategory(numeric_Start, divChild);
		}
		return target;
	}

	/**
	 * 將文字轉數字
	 * @param index
	 * @param article 整篇文章
	 * */
	private Integer getNumberFromEachCategory(int index, String article) {
		Integer sum = 0;
		while (Character.isDigit(article.charAt(index))) {
			sum = sum*10 + Character.getNumericValue(article.charAt(index));
			index++;
		}
		return sum;
	}

	/**
	 * 有境外有本土時使用到<br>
	 * ex. ...確定病例，分別為1例本土個案及6例境外移入;<br>
	 * 擷取從第一個逗號到第一個分號
	 * @param comma 逗號
	 * @param semicolon 分號
	 * @param divChild 整篇新聞
	 * @return String 分別為1例本土個案及6例境外移入
	 * */
	private String getDomesticOrImportedCase(String comma,String semicolon,String divChild){
		//擷取起點
		Integer start = 0;
		// 擷取終點
		Integer end = 0;
		log.info("第一個逗點的位置:{}",divChild.indexOf(comma));
		if(divChild.indexOf(comma)<50){
			//為第一行
			start = divChild.indexOf(comma)+ comma.length();
			end = divChild.indexOf(semicolon);
			return divChild.substring(start,end);
		}
		return null;
	}

	/**
	 * 判斷是否全部是境外移入
	 * @param keyWord ex. 國內新增6例
	 * @param semicolon 第一個分號為確定病例結束
	 * @param divChild 整篇新聞
	 * @return Boolean true:全部是境外移入
	 * */
	private Boolean isAllImported(String keyWord,String semicolon,String divChild){
		//擷取起點
		Integer start = 0;
		// 擷取終點
		Integer end = 0;
		if(divChild.indexOf(keyWord)!=-1){
			start = divChild.indexOf(keyWord)+ keyWord.length();
			end = divChild.indexOf(semicolon);
			String target = divChild.substring(start,end);
			return properties.ALL_IMPORTED.equals(target);
		}
		return false;
	}

	/**
	 * 新聞相關參數配置<br>
	 * 對應 webcrawl.properties(prefix = news)<br>
	 * @author chris
	 * */
	@ConstructorBinding
	@ConfigurationProperties(prefix = "news")
	public static class properties {
		/** http://at.cdc.tw/YEc68Q */
		public static String URL;
		/** https://www.cdc.gov.tw */
		public static StringBuilder PREFIX;
		/** 確定病例 */
		public static String TITLE_NAME;
		/** 國內新增 */
		public static String DOMESTIC_NEW_NUM;
		/** 校正回歸本土個案 */
		public static String RETURN_NUM;
		/** 確診個案中新增 */
		public static String DEATH_NUM;
		/** COVID-19境外移入確定病例 */
		public static String ALL_IMPORTED;

		public properties(String URL, StringBuilder PREFIX, String TITLE_NAME, String DOMESTIC_NEW_NUM, String RETURN_NUM, String DEATH_NUM) {
			this.URL = URL;
			this.PREFIX = PREFIX;
			this.TITLE_NAME = TITLE_NAME;
			this.DOMESTIC_NEW_NUM = DOMESTIC_NEW_NUM;
			this.RETURN_NUM = RETURN_NUM;
			this.DEATH_NUM = DEATH_NUM;
		}
	}
}
