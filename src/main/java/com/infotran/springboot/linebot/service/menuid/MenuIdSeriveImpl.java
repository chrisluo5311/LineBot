package com.infotran.springboot.linebot.service.menuid;


import com.infotran.springboot.linebot.dao.MenuIdRepository;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MenuIdSeriveImpl implements MenuIdService {

	private static String LOG_PREFIX = "[MenuIdSeriveImpl]";

	@Autowired
	MenuIdRepository menuRepo;
	
	@Override
	public MenuID save(MenuID menuid) {
		String id = menuid.getMenuId();
		MenuID menuID =getMenuID(id);
		if (id.length()!=0 && menuID!=null) {
			menuRepo.deleteAll();
			log.info("{} LineBot 目錄id: {},",LOG_PREFIX,id);
			menuRepo.save(menuid);
			return menuid;
		}
		return null;
	}

	@Override
	public MenuID getMenuID(String menuid){
		return  menuRepo.findByMenuId(menuid);
	}

	@Override
	public MenuID getMenuByName(String menuName){
		return  menuRepo.findByMenuName(menuName);
	}
}
