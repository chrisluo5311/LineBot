package com.infotran.springboot.ConfirmCase.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.infotran.springboot.annotation.LogInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@Slf4j
public class CrawlCovidNumbers {

	private static final String CDC_URL = "http://at.cdc.tw/YEc68Q";// 新聞首頁

	private static String CDC_NewsDetail = "";// 進入新聞

	private static final String titleName = "指揮中心公布新增";
	
	private static final String newstr = "國內新增";
	
	private static final String restr = "校正回歸本土個案";
	
	private static final String deathstr = "確診個案中新增";

	@Autowired
	private ConfirmCaseService cService;
	
	private OkHttpClient client;

	/*
	 * Run the crawl
	 */
	public void run() throws IOException {
		client = new OkHttpClient();
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
				log.info(body);
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
			String tname = element.select(".content-boxes-v3 > a").attr("title").substring(0,8);
			if (todayMap.containsKey(month) && todayMap.containsValue(date) && titleName.equals(tname)) {
				res.append(element.select(".content-boxes-v3 > a").attr("href"));
				return res.toString();
			}
		}
		return res.toString();
	}

	/*
	 * Parse the detailed news body and save the confirmed numbers
	 */
	private void parseBody(String detailedURL) {
		try {
			Document doc = Jsoup.connect(detailedURL).timeout(3000).maxBodySize(0).get();
			Elements divchildren = doc.select("div.news-v3-in > div ");
			String divchild = divchildren.text();
			int numeric_Start = 0;//數字起點
			numeric_Start = divchild.indexOf(newstr)+newstr.length();// 數字起點
			int newNum = getStringNumber(numeric_Start,divchild);
			numeric_Start = divchild.indexOf(restr)+restr.length();
			int reNum = getStringNumber(numeric_Start,divchild);
			int totalNum = newNum + reNum;
			numeric_Start = divchild.indexOf(deathstr)+deathstr.length();
			int deathNum = getStringNumber(numeric_Start,divchild);
			ConfirmCase cfc = ConfirmCase.builder()
										 .todayAmount(newNum)
										 .returnAmount(reNum)
										 .totalAmount(totalNum)
										 .deathAmount(deathNum)
										 .build();
			cService.save(cfc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int getStringNumber(int index,String article) {
		int sum = 0;
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
