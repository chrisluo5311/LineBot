package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.LineClientInterface;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.richmenu.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@Order(1)
public class CreateRichMenu implements LineClientInterface, CommandLineRunner {

	@Resource
	MenuIdService menuService;

	@Override
	public void run(String... args) throws Exception {
		if (isRichMenuExists())return;
		executeCreateRichMenu();
	}

	public void executeCreateRichMenu() throws Exception {
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
		//action01
		MessageAction messageAction = new MessageAction("今日確診","查詢今日確診");
		RichMenuArea todayNum = new RichMenuArea(new RichMenuBounds(0, 0, 836, 846),messageAction);
		//action2
		MessageAction action02 = new MessageAction("查看所在位置口罩剩餘狀態","查看所在位置口罩剩餘狀態");
		RichMenuArea BuyMask = new RichMenuArea(new RichMenuBounds(833, 2, 836, 844),action02);
		//action3
		MessageAction action03 = new MessageAction("查看所在位置與確診足跡","查看所在位置與確診足跡");
		RichMenuArea locationStatus = new RichMenuArea(new RichMenuBounds(1666, 3, 834, 843),action03);
		//action4
		PostbackAction action04 = PostbackAction.builder().label("國內外疫情").data("國內外疫情").displayText("國內外疫情").build();
		RichMenuArea globalStatus = new RichMenuArea(new RichMenuBounds(0, 846, 835, 840),action04);
		//action5
		PostbackAction action05 = PostbackAction.builder().label("施打疫苗統計").data("施打疫苗統計").displayText("施打疫苗統計").build();
		RichMenuArea vaccineReport = new RichMenuArea(new RichMenuBounds(833, 846, 833, 840),action05);
		//action6
		PostbackAction action06 = PostbackAction.builder().label("其他").data("其他").displayText("其他").build();
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
	 * false:沒有目錄<br>
	 * true:有目錄
	 *
	 * @return boolean
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private static boolean isRichMenuExists() throws ExecutionException, InterruptedException {
		List<RichMenuResponse> richmenuresponse = client.getRichMenuList().get().getRichMenus();
		String ans = "";
		for (RichMenuResponse res : richmenuresponse){
			 ans = res.getRichMenuId();
		}
		return (ans.length()==0)?false:true;
	}






}
