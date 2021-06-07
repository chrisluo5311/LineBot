package com.infotran.springboot.MenuID.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infotran.springboot.MenuID.Model.MenuID;

public interface MenuIdRepository extends JpaRepository<MenuID, Integer>{

}
