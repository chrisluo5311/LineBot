package com.infotran.springboot.linebot.service;


import com.infotran.springboot.linebot.model.MenuID;
import org.springframework.stereotype.Service;

/**
 * @author chris
 */
@Service
public interface MenuIdService {

	/**
	 * 新增 MenuID
	 * @param menuId RichMenu id
	 * @return MenuID
	 * */
	MenuID save(MenuID menuId);

	/**
	 * 依 MenuID 查找 MenuID
	 * @param menuId RichMenu id
	 * @return MenuID
	 *
	 * */
	MenuID getMenuId(String menuId);

	/**
	 * 依 menuName 查找 MenuID
	 * @param menuName RichMenu Name
	 * @return MenuID
	 *
	 * */
	MenuID getMenuByName(String menuName);

	/**
	 * 刪除全部
	 *
	 * */
	void deleteAll();
}
