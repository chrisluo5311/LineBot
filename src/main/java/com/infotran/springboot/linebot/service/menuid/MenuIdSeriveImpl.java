package com.infotran.springboot.linebot.service.menuid;


import com.infotran.springboot.linebot.dao.MenuIdRepository;
import com.infotran.springboot.linebot.model.MenuID;
import com.infotran.springboot.linebot.service.MenuIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuIdSeriveImpl implements MenuIdService {

	@Autowired
	MenuIdRepository menuRepo;
	
	@Override
	public MenuID save(MenuID menuid) {
		String id = menuid.getMenuId();
		if (id.length()!=0) {
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
