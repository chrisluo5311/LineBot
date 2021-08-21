package com.infotran.springboot.LineBot.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.infotran.springboot.LineBot.service.LineMessaging;
import com.linecorp.bot.model.richmenu.RichMenuListResponse;
import com.linecorp.bot.model.richmenu.RichMenuResponse;

public class DeleteRichMenu implements LineMessaging{

//	public static void main (String[] args) {
//		try {
//			client.cancelDefaultRichMenu();
//			client.unlinkRichMenuIdFromUser("all");
//			RichMenuListResponse response = client.getRichMenuList().get();
//			List<RichMenuResponse> listResponse = response.getRichMenus();
//			String menuId = null;
//			for (RichMenuResponse rm : listResponse) {
//				menuId = rm.getRichMenuId();
//			}
//			client.deleteRichMenu("richmenu-c3c97be0387cab4c8032da28546405de").get();
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}
//
//
//	}
	
}
