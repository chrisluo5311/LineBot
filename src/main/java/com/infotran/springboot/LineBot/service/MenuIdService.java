package com.infotran.springboot.LineBot.service;


import com.infotran.springboot.LineBot.Model.MenuID;

public interface MenuIdService {

	public MenuID save(MenuID menuid);

	public MenuID getMenuID(String menuid);

	public MenuID getMenuByName(String menuName);
}
