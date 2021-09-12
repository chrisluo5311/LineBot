package com.infotran.springboot.linebot.dao;

import com.infotran.springboot.linebot.model.MenuID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuIdRepository extends JpaRepository<MenuID, Integer>{
    public MenuID findByMenuId(String menuid);

    public MenuID findByMenuName(String menuName);
}
