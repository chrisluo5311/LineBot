package com.infotran.springboot.linebot.service.menuid;


import com.infotran.springboot.linebot.dao.MenuIdRepository;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author chris
 */
@Service
@Slf4j
public class MenuIdServiceImpl implements MenuIdService {

	private static final String LOG_PREFIX = "[MenuIdServiceImpl]";

	@Resource
	MenuIdRepository menuRepo;
	
	@Override
	public MenuID save(MenuID menuid) {
		String id = menuid.getMenuId();
		MenuID menuId =getMenuId(id);
		if (id.length()!=0 && menuId!=null) {
			menuRepo.deleteAll();
			log.info("{} LineBot 新的目錄id: {},",LOG_PREFIX,id);
			menuRepo.save(menuid);
			return menuid;
		}
		return null;
	}

	@Override
	public MenuID getMenuId(String menuId){
		return  menuRepo.findByMenuId(menuId);
	}

	@Override
	public MenuID getMenuByName(String menuName){
		return  menuRepo.findByMenuName(menuName);
	}
}
