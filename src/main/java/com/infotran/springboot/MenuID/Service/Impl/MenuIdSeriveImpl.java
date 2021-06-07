package com.infotran.springboot.MenuID.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infotran.springboot.MenuID.Dao.MenuIdRepository;
import com.infotran.springboot.MenuID.Model.MenuID;
import com.infotran.springboot.MenuID.Service.MenuIdService;

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
	
}
