package com.infotran.springboot.linebot.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.richmenu.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.infotran.springboot.linebot.service.LineReplyMessageHandler;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.response.BotApiResponse;

@SpringBootApplication
public class CreateRichMenu implements LineReplyMessageHandler,ApplicationRunner {

	@Autowired
	MenuIdService menuService;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(CreateRichMenu.class, args);
	}

	private byte[] File2Byte() {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(new File("D:\\_SpringBoot\\image\\menufinal.jpg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[81920];
			int len = 0;
			while ((len = fis.read(b)) != -1) {
				baos.write(b, 0, len);
			}
			fis.close();
			baos.close();
			buffer = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	private  List<RichMenuArea> createRichMenuArea(){
		List<RichMenuArea> area = new ArrayList<>();
		PostbackAction action01 = PostbackAction.builder().label("今日確診").data("1").displayText("今日確診").build();
		RichMenuArea todayNum = new RichMenuArea(new RichMenuBounds(0, 0, 836, 846),action01);
		PostbackAction action02 = PostbackAction.builder().label("買口罩").data("2").displayText("買口罩").build();
		RichMenuArea BuyMask = new RichMenuArea(new RichMenuBounds(833, 2, 836, 844),action02);
		PostbackAction action03 = PostbackAction.builder().label("位置狀況").data("3").displayText("位置狀況").build();
		RichMenuArea locationStatus = new RichMenuArea(new RichMenuBounds(1666, 3, 834, 843),action03);
		PostbackAction action04 = PostbackAction.builder().label("國內外疫情").data("4").displayText("國內外疫情").build();
		RichMenuArea globalStatus = new RichMenuArea(new RichMenuBounds(0, 846, 835, 840),action04);
		PostbackAction action05 = PostbackAction.builder().label("施打疫苗統計").data("5").displayText("施打疫苗統計").build();
		RichMenuArea vaccineReport = new RichMenuArea(new RichMenuBounds(833, 846, 833, 840),action05);
		PostbackAction action06 = PostbackAction.builder().label("其他").data("6").displayText("其他").build();
		RichMenuArea others = new RichMenuArea(new RichMenuBounds(1663, 843, 835, 843),action06);
		area.add(todayNum);
		area.add(BuyMask);
		area.add(locationStatus);
		area.add(globalStatus);
		area.add(vaccineReport);
		area.add(others);
		return area;
	}

	/**
	 * false:no rich menu;
	 * true:rich menu exist;
	 */
	private static boolean isRichMenuExists() throws ExecutionException, InterruptedException {
		List<RichMenuResponse> richmenuresponse = client.getRichMenuList().get().getRichMenus();
		String ans = "";
		for (RichMenuResponse res : richmenuresponse){
			 ans = res.getRichMenuId();
		}
		return (ans.equals(""))?false:true;
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (isRichMenuExists())return;
		List<RichMenuArea> area = createRichMenuArea();
		RichMenu richmenu = RichMenu.builder().size(new RichMenuSize(2500, 1686)).selected(true).name("covidMenu")
				.chatBarText("功能選單").areas(area).build();
		byte[] buffer = File2Byte();
		try {
			RichMenuIdResponse menuResponse = client.createRichMenu(richmenu).get();
			String menuId = menuResponse.getRichMenuId();
			MenuID menu = MenuID.builder().menuId(menuId).menuName(richmenu.getName()).build();
			menuService.save(menu);
			BotApiResponse apiResponse = blobClient.setRichMenuImage(menuId, "image/jpeg", buffer).get();
			client.linkRichMenuIdToUser("all", menuId).get();
			client.setDefaultRichMenu(menuId).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void postBackReply(PostbackEvent event) throws Exception {

	}
}
