package com.infotran.springboot.linebot.controller;

import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.LineClientInterface;
import com.infotran.springboot.linebot.service.MenuIdService;
import com.infotran.springboot.util.HandleFileUtil;
import com.linecorp.bot.model.action.CameraAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.richmenu.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
	private static final String RICH_MENU_FILE_PATH = "D:/IdeaProject/LineBot/src/main/resources/static/menuFinal.jpg";

	@Resource
	MenuIdService menuService;

	@Override
	public void run(String... args) {
		if (!isRichMenuExists()){
			createRichMenu();
		}
	}

	/**
	 * 製做RichMenu
	 * @throws Exception 製作 Line RichMenu失敗
	 * */
	public void createRichMenu() {
		List<RichMenuArea> area = createRichMenuArea();
		RichMenu richmenu = RichMenu.builder()
									.size(new RichMenuSize(2500, 1686))
									.selected(true)
									.name(MENU_NAME)
									.chatBarText(CHAT_BAR_TEXT)
									.areas(area)
									.build();
		byte[] buffer = HandleFileUtil.file2Byte(RICH_MENU_FILE_PATH);
		Assert.noNullElements(Collections.singleton(buffer),"LineBot RichMenu照片不可為null，請檢查FilePath!!!");
		try {
			RichMenuIdResponse menuResponse = CLIENT.createRichMenu(richmenu).get();
			String menuId = menuResponse.getRichMenuId();
			MenuID menu = MenuID.builder().menuId(menuId).menuName(richmenu.getName()).build();
			menuService.save(menu);
			BLOB_CLIENT.setRichMenuImage(menuId, "image/jpeg", buffer).get();
			CLIENT.linkRichMenuIdToUser("all", menuId).get();
			CLIENT.setDefaultRichMenu(menuId).get();
			log.info("Rich Menu 創建成功 Menu Id:{}",menuId);
		} catch (InterruptedException | ExecutionException e) {
			log.error("製作Line RichMenu失敗 : {}",e.getMessage());
		}
	}

	/**
	 * 製做RichMenuArea<br>
	 * 例: 長、寬、功能名稱
	 * @return List RichMenuArea
	 * */
	private List<RichMenuArea> createRichMenuArea(){
		List<RichMenuArea> area = new ArrayList<>();
		//查詢今日確診 action01
		MessageAction messageAction = new MessageAction("今日確診","查詢今日確診");
		RichMenuArea todayNum = new RichMenuArea(new RichMenuBounds(0, 0, 836, 846),messageAction);

		//查看所在位置口罩剩餘狀態 action2
		MessageAction action02 = new MessageAction("查看所在位置口罩剩餘狀態","查看所在位置口罩剩餘狀態");
		RichMenuArea buyMask = new RichMenuArea(new RichMenuBounds(833, 2, 836, 844),action02);

		//掃描QRCode action3
		CameraAction action03 = CameraAction.builder().label("掃描QRCode").build();
		RichMenuArea qrcodeScanner = new RichMenuArea(new RichMenuBounds(1666, 3, 834, 843),action03);

		//國外疫情 action4
		PostbackAction action04 = PostbackAction.builder().label("國外疫情").data("國外疫情").displayText("國外疫情").build();
		RichMenuArea globalStatus = new RichMenuArea(new RichMenuBounds(0, 846, 835, 840),action04);

		//查看統計圖 action5
		MessageAction action05 = new MessageAction("查看統計圖","查看統計圖");
		RichMenuArea vaccineReport = new RichMenuArea(new RichMenuBounds(833, 846, 833, 840),action05);

		//其他 action6
		PostbackAction action06 = PostbackAction.builder().label("其他").data("其他").displayText("其他").build();
		RichMenuArea others = new RichMenuArea(new RichMenuBounds(1663, 843, 835, 843),action06);
		area.add(todayNum);
		area.add(buyMask);
		area.add(qrcodeScanner);
		area.add(globalStatus);
		area.add(vaccineReport);
		area.add(others);
		return area;
	}

	/**
	 * 檢查是否有RichMenu存在<br>
	 * false: 沒目錄、要創建<br>
	 * true: 有目錄
	 *
	 * @return boolean
	 */
	private boolean isRichMenuExists() {
		try {
			List<RichMenuResponse>  richMenuResponseList = CLIENT.getRichMenuList().get().getRichMenus();
			log.info("RichMenu數量:{}",richMenuResponseList.size());
			//空的則創建
			if(richMenuResponseList.size()==0) {
				log.info("查詢RichMenu不存在 開始創建");
				return false;
			}
			//超過1個則先刪除再創建
			if(richMenuResponseList.size()>1){
				richMenuResponseList.forEach(x->CLIENT.deleteRichMenu(x.getRichMenuId()));
				menuService.deleteAll();
				log.warn("RichMenu數量超過1個 進行全部刪除 開始重建");
				return false;
			}
			//1個 檢查db有無紀錄
			for (RichMenuResponse res : richMenuResponseList){
				MenuID menu = menuService.getMenuId(res.getRichMenuId());
				if(Objects.isNull(menu)){
					MenuID saveResult = menuService.save(
							MenuID.builder()
									.menuId(res.getRichMenuId())
									.menuName(res.getName())
									.build()
					);
					if(Objects.isNull(saveResult)){
						log.error("查詢RichMenu存在 但MenuID新增db失敗");
					}
				}
				log.info("查詢RichMenu已存在 不需重建");
				return true;
			}
		} catch (InterruptedException | ExecutionException e) {
			log.error("獲取Linebot Menu失敗:{}",e.getMessage());
		}
		return false;
	}

}
