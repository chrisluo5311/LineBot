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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 針對
 * 1.當日新增數目
 * 2.確診案例中分佈
 * 3.校正回歸
 * 4.死亡數目
 * 進行爬蟲
 *
 * @author chris
 */
@Service
@Slf4j
public class GetCovidNumService implements ClientUtil {

	private static final String LOG_PREFIX = "CrawlCovidNumbers";

	/** 新聞首頁 */
	@Value("${CDC.URL}")
	public String CDC_URL;
	/** CDC網站前綴 */
	@Value("${CDC_URL_PREFIX}")
	private StringBuilder CDC_URL_PREFIX;
	/** 新聞標題名 */
	@Value("${TITLE_NAME}")
	private String TITLE_NAME;
	/** 國內新增確診人數 */
	@Value("${DOMESTIC_NEWNUM}")
	private String DOMESTIC_NEWNUM;
	/** 校正回歸數 */
	@Value("${RETURN_NUM}")
	private String RETURN_NUM;
	/** 死亡人數 */
	@Value("${DEATH_NUM}")
	private String DEATH_NUM;
	/** confirm case redis key name */
	@Value("${CONFIRMCASE_REDIS_KEY}")
	private String CONFIRMCASE_REDIS_KEY;

	@Resource
	private ConfirmCaseService confirmCaseService;

	@Resource
	RedisTemplate<Object, ConfirmCase> confirmCaseRedisTemplate;

	/** 新聞body */
	String divChild;

	/**
	 * Parse first CDC URL
	 * Get detailed news URL
	 * @param body 疾管局新闻首页的连结
	 * @return ConfirmCase
	 */
	public ConfirmCase getUrlOfNewsDetail(String body) throws LineBotException {
		Document doc = Jsoup.parse(body);
		Elements newsLists = doc.select(".cbp-item");
		Map<String, String> todayMap = TimeUtil.genTodayDate();
		for (Element element : newsLists) {
			String month = element.select("p.icon-year").text().substring(7);
			String date = element.select("p.icon-date").text();
			String tName = element.select(".content-boxes-v3 > a").attr("title");
			if (todayMap.containsKey(month) && todayMap.containsValue(date) && tName.indexOf(TITLE_NAME) != -1) {
				CDC_URL_PREFIX.append(element.select(".content-boxes-v3 > a").attr("href"));
				return parseBody(CDC_URL_PREFIX.toString());
			}
		}
		log.error("{} 找不到新聞標題",LOG_PREFIX);
		throw new LineBotException(LineBotExceptionEnums.NEWS_TITLE_CHANGE);
	}

	/**
	 * Parse the detailed news body
	 * Save the confirmed numbers
	 * @param detailedURL 当日新增确诊数目的新闻连结
	 * @return ConfirmCase
	 */
	public ConfirmCase parseBody(String detailedURL) {
		ConfirmCase confirmCase = null;
		try {
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divChildren = doc.select("div.news-v3-in > div ");
			divChild = divChildren.text();
			//今日確診數
			Integer newNum = getNumFromDivChild(DOMESTIC_NEWNUM);
			//校正回歸數
			Integer reNum = getNumFromDivChild(RETURN_NUM);
			//確診案例中分佈(境外移入還是本土)
			String domesticOrImportedCase = getDomesticOrImportedCase("，","；");
			//總數
			Integer totalNum = newNum + reNum;
			//死亡人數
			Integer deathNum = getNumFromDivChild(DEATH_NUM);
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
			confirmCase = confirmCaseService.save(cfc);
			if(Objects.isNull(confirmCase)){
				log.error("confirmCase 爬蟲成功 但新增至db失敗");
			}
		} catch (IOException e) {
			throw new LineBotException(LineBotExceptionEnums.FAIL_ON_SSLHELPER_CONNECTION,e.getMessage());
		} finally {
			return confirmCase;
		}
	}

	/**
	 * 根據關鍵字取得不同的確診數目
	 * @param keyword 搜索的關鍵字
	 * @return Integer 確診數目
	 * */
	private Integer getNumFromDivChild(String keyword) {
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

	private Integer getNumberFromEachCategory(int index, String article) {
		Integer sum = 0;
		while (Character.isDigit(article.charAt(index))) {
			sum = sum*10 + Character.getNumericValue(article.charAt(index));
			index++;
		}
		return sum;
	}

	private String getDomesticOrImportedCase(String comma,String semicolon){
		//擷取起點
		Integer start = 0;
		// 擷取終點
		Integer end = 0;
		if(divChild.indexOf(comma)!=-1){
			start = divChild.indexOf(comma)+ comma.length();
			end = divChild.indexOf(semicolon);
			return divChild.substring(start,end);
		}
		return null;
	}

}
