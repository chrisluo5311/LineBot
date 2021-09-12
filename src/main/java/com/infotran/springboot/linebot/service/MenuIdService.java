package com.infotran.springboot.linebot.service;


import com.infotran.springboot.linebot.model.MenuID;
import org.springframework.stereotype.Service;

@Service
public interface MenuIdService {

	public MenuID save(MenuID menuid);

	public MenuID getMenuID(String menuid);

	public MenuID getMenuByName(String menuName);
}
