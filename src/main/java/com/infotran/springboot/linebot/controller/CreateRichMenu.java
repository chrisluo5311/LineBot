package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.exception.LineBotException;
import com.infotran.springboot.exception.exceptionenum.LineBotExceptionEnums;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.LineClientInterface;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.infotran.springboot.util.HandleFileUtil;
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

/**
 * @author chris
 */
@Component
@Slf4j
@Order(1)
public class CreateRichMenu implements LineClientInterface, CommandLineRunner {

	/**	menu名字 */
	private static final String MENU_NAME = "CovidMenu";

	/** chatBarText功能選單 */
	private static final String CHAT_BAR_TEXT = "功能選單";

	/** richMenu的照片檔案路徑 */
	private static final String RICH_MENU_FILE_PATH = "/static/menuFinal.jpg";

	@Resource
	MenuIdService menuService;

	@Override
	public void run(String... args) throws Exception {
		//刪除MENU 目前需手動刪除
		if (!isRichMenuExists()){
			executeCreateRichMenu();
		}
	}

	/**
	 * 製做RichMenu
	 * @throws Exception 製作Line RichMenu失敗
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
		byte[] buffer = HandleFileUtil.file2Byte(RICH_MENU_FILE_PATH);
		try {
			RichMenuIdResponse menuResponse = CLIENT.createRichMenu(richmenu).get();
			String menuId = menuResponse.getRichMenuId();
			MenuID menu = MenuID.builder().menuId(menuId).menuName(richmenu.getName()).build();
			menuService.save(menu);
			BLOB_CLIENT.setRichMenuImage(menuId, "image/jpeg", buffer).get();
			CLIENT.linkRichMenuIdToUser("all", menuId).get();
			CLIENT.setDefaultRichMenu(menuId).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new LineBotException(LineBotExceptionEnums.FAIL_ON_CREATE_RICHMENU,e.getMessage());
		}
	}

	/**
	 * 製做RichMenuArea<br>
	 * 例: 長、寬、功能名稱
	 * @return List RichMenuArea
	 * */
	private  List<RichMenuArea> createRichMenuArea(){
		List<RichMenuArea> area = new ArrayList<>();
		//查詢今日確診 action01
		MessageAction messageAction = new MessageAction("今日確診","查詢今日確診");
		RichMenuArea todayNum = new RichMenuArea(new RichMenuBounds(0, 0, 836, 846),messageAction);

		//查看所在位置口罩剩餘狀態 action2
		MessageAction action02 = new MessageAction("查看所在位置口罩剩餘狀態","查看所在位置口罩剩餘狀態");
		RichMenuArea buyMask = new RichMenuArea(new RichMenuBounds(833, 2, 836, 844),action02);

		//掃描QRCode action3
		//todo 新功能 刪除上次menu
		MessageAction action03 = new MessageAction("掃描QRCode","掃描QRCode");
		RichMenuArea locationStatus = new RichMenuArea(new RichMenuBounds(1666, 3, 834, 843),action03);

		//國外疫情 action4
		//TODO 名稱改 國外疫情
		PostbackAction action04 = PostbackAction.builder().label("國外疫情").data("國外疫情").displayText("國外疫情").build();
		RichMenuArea globalStatus = new RichMenuArea(new RichMenuBounds(0, 846, 835, 840),action04);

		//查看統計圖 action5
		//TODO 名稱改 查看統計圖
		MessageAction action05 = new MessageAction("查看統計圖","查看統計圖");
		RichMenuArea vaccineReport = new RichMenuArea(new RichMenuBounds(833, 846, 833, 840),action05);

		//其他 action6
		PostbackAction action06 = PostbackAction.builder().label("其他").data("其他").displayText("其他").build();
		RichMenuArea others = new RichMenuArea(new RichMenuBounds(1663, 843, 835, 843),action06);

		area.add(todayNum);
		area.add(buyMask);
		area.add(locationStatus);
		area.add(globalStatus);
		area.add(vaccineReport);
		area.add(others);
		return area;
	}

	/**
	 * 檢查是否有RichMenu存在<br>
	 * false: 沒有目錄<br>
	 * true:  有目錄
	 *
	 * @return boolean
	 */
	private static boolean isRichMenuExists() {
		List<RichMenuResponse> richMenuResponseList ;
		try {
			richMenuResponseList = CLIENT.getRichMenuList().get().getRichMenus();
			for (RichMenuResponse res : richMenuResponseList){
				//只有一個才能這樣判定
				return res.getRichMenuId().length() != 0;
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}






}
