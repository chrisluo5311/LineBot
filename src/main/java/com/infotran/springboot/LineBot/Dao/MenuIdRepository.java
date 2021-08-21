package com.infotran.springboot.LineBot.Dao;

import com.infotran.springboot.LineBot.Model.MenuID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MenuIdRepository extends JpaRepository<MenuID, Integer>{
    public MenuID findByMenuId(String menuid);

    public MenuID findByMenuName(String menuName);
}
