package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.LineClientInterface;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.infotran.springboot.util.DownloadFileUtil;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.richmenu.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@Order(1)
public class CreateRichMenu implements LineClientInterface, CommandLineRunner {

	//menu名字
	private static final String MENU_NAME = "CovidMenu";

	//chatBarText功能選單
	private static final String CHAT_BAR_TEXT = "功能選單";

	//richMenu的照片檔案路徑
	private static final String richMenuFilePath = "/static/menufinal.jpg";

	@Resource
	MenuIdService menuService;

	@Override
	public void run(String... args) throws Exception {
		if (isRichMenuExists())return;
		executeCreateRichMenu();
	}

	/**
	 * 製做RichMenu
	 *
	 * */
	public void executeCreateRichMenu() throws Exception {
		List<RichMenuArea> area = createRichMenuArea();
		RichMenu richmenu = RichMenu.builder()
									.size(new RichMenuSize(2500, 1686))
									.selected(true)
									.name(MENU_NAME)
									.chatBarText(CHAT_BAR_TEXT)
									.areas(area)
									.build();
		byte[] buffer = DownloadFileUtil.file2Byte(richMenuFilePath);
		try {
			RichMenuIdResponse menuResponse = client.createRichMenu(richmenu).get();
			String menuId = menuResponse.getRichMenuId();
			MenuID menu = MenuID.builder().menuId(menuId).menuName(richmenu.getName()).build();
			menuService.save(menu);
			blobClient.setRichMenuImage(menuId, "image/jpeg", buffer).get();
			client.linkRichMenuIdToUser("all", menuId).get();
			client.setDefaultRichMenu(menuId).get();
		} catch (InterruptedException | ExecutionException e) {
			log.info("RichMenu 創建失敗");
			e.printStackTrace();
		}
	}


	private  List<RichMenuArea> createRichMenuArea(){
		List<RichMenuArea> area = new ArrayList<>();
		//查詢今日確診 action01
		MessageAction messageAction = new MessageAction("今日確診","查詢今日確診");
		RichMenuArea todayNum = new RichMenuArea(new RichMenuBounds(0, 0, 836, 846),messageAction);
		//查看所在位置口罩剩餘狀態 action2
		MessageAction action02 = new MessageAction("查看所在位置口罩剩餘狀態","查看所在位置口罩剩餘狀態");
		RichMenuArea BuyMask = new RichMenuArea(new RichMenuBounds(833, 2, 836, 844),action02);
		//查看所在位置與確診足跡 action3
		MessageAction action03 = new MessageAction("查看所在位置與確診足跡","查看所在位置與確診足跡");
		RichMenuArea locationStatus = new RichMenuArea(new RichMenuBounds(1666, 3, 834, 843),action03);
		//action4
		PostbackAction action04 = PostbackAction.builder().label("國內外疫情").data("國內外疫情").displayText("國內外疫情").build();
		RichMenuArea globalStatus = new RichMenuArea(new RichMenuBounds(0, 846, 835, 840),action04);
		//查看疫苗施打人數累計統計圖 action5
//		PostbackAction action05 = PostbackAction.builder().label("施打疫苗統計").data("施打疫苗統計").displayText("施打疫苗統計").build();
		MessageAction action05 = new MessageAction("查看疫苗施打人數累計統計圖","查看疫苗施打人數累計統計圖");
		RichMenuArea vaccineReport = new RichMenuArea(new RichMenuBounds(833, 846, 833, 840),action05);
		//其他 action6
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
	 * 檢查是否有RichMenu存在
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
