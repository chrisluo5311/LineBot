package com.infotran.springboot.ConfirmCase.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.infotran.springboot.annotation.LogInfo;
import com.infotran.springboot.config.SSLHelper;
import com.infotran.springboot.config.SSLSocketClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.infotran.springboot.ConfirmCase.model.ConfirmCase;
import com.infotran.springboot.ConfirmCase.service.ConfirmCaseService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.X509TrustManager;

@Controller
@Slf4j
public class CrawlCovidNumbers {

	private final String CDC_URL = "http://at.cdc.tw/YEc68Q";// 新聞首頁

	private String CDC_NewsDetail = "";// 進入新聞

	private final String titleName = "指揮中心公布新增";
	
	private final String newstr = "國內新增";
	
	private final String restr = "校正回歸本土個案";
	
	private final String deathstr = "確診個案中新增";

	@Autowired
	private ConfirmCaseService cService;


	private static OkHttpClient client ;

	static {
		X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
		client = new OkHttpClient.Builder()
				.sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)// 忽略校验
				.hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())//忽略校验
				.build();
	}

	/*
	 * Run the crawl
	 */
	@Scheduled(cron = "0 0/5 14 * * ?")
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
//				log.info("This is body ===>" + body);
				CDC_NewsDetail = getNewsDetailURL(body);
				parseBody(CDC_NewsDetail);
			}
		});
	}

	/*
	 * Parse first CDC URL and get the detailed news URL
	 */
	private String getNewsDetailURL(String body) {
		StringBuilder res = new StringBuilder("https://www.cdc.gov.tw");
		Document doc = Jsoup.parse(body);
		Elements newslists = doc.select(".cbp-item");
		Map<String, String> todayMap = genTodayDate();
		for (Element element : newslists) {
			String month = element.select("p.icon-year").text().substring(7,8);
			String date = element.select("p.icon-date").text();
			String tname = element.select(".content-boxes-v3 > a").attr("title");
			if (todayMap.containsKey(month) && todayMap.containsValue(date) && tname.indexOf("確定病例") != -1) {
				log.info("The rest of the news url is here ====> " + element.select(".content-boxes-v3 > a").attr("href"));
				res.append(element.select(".content-boxes-v3 > a").attr("href"));
				return res.toString();
			}
		}
		log.info("this is newsdetail url ==> "+res.toString());
		return res.toString();
	}

	/*
	 * Parse the detailed news body and save the confirmed numbers
	 */
	private void parseBody(String detailedURL) {
		try {
			log.info("Method parameter is " + detailedURL);
			log.info("parseBody method starts here");
			Document doc = SSLHelper.getConnection(detailedURL).timeout(3000).maxBodySize(0).get();
//			Document doc = Jsoup.connect(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divchildren = doc.select("div.news-v3-in > div ");
			String divchild = divchildren.text();
			Integer numeric_Start = 0;//數字起點
			numeric_Start = divchild.indexOf(newstr)+newstr.length();// 數字起點
			Integer newNum = getStringNumber(numeric_Start,divchild);
			Integer reNum = 0;
			if (divchild.indexOf(restr) != -1) {
				numeric_Start = divchild.indexOf(restr)+restr.length();
				reNum = getStringNumber(numeric_Start,divchild);
			}
			Integer totalNum = newNum + reNum;
			Integer deathNum = 0;
			if (divchild.indexOf(deathstr) != -1) {
				numeric_Start = divchild.indexOf(deathstr)+deathstr.length();
				deathNum = getStringNumber(numeric_Start,divchild);
			}
			log.info("新增數目{},校正回歸{},總數{},死亡數目{}",newNum,reNum,totalNum,deathNum);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .build();
			log.info("Before saving into db");
			cService.save(cfc);
			log.info("Saving db finished");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getStringNumber(int index,String article) {
		Integer sum = 0;
		while (Character.isDigit(article.charAt(index))) {
			sum = sum*10 + Character.getNumericValue(article.charAt(index));
			index++;
		}
		return sum;
	}
	

	private static Map<String, String> genTodayDate() {
		Map<String, String> date = new HashMap<>();
		LocalDate now = LocalDate.now();
		date.put(String.valueOf(now.getMonthValue()), String.valueOf(now.getDayOfMonth()));
		return date;
	}

}
