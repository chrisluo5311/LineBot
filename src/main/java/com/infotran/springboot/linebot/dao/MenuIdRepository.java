package com.infotran.springboot.linebot.dao;

import com.infotran.springboot.linebot.model.MenuID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chris
 */
public interface MenuIdRepository extends JpaRepository<MenuID, Integer>{

    /**
     * 依 MenuId查找 MenuID
     * @param menuId RichMenu Id
     * @return MenuID
     * */
    MenuID findByMenuId(String menuId);

    /**
     * 依 MenuName查找 MenuID
     * @param menuName RichMenu Name
     * @return MenuID
     * */
    MenuID findByMenuName(String menuName);
}
