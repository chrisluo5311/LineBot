package com.infotran.springboot.webcrawler.vaccinesvg.dao;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * 各疫苗接踵累计人次
 * VaccinedTypePelpleRepo
 * @author chris
 */
public interface VaccinedTypePeopleRepo extends JpaRepository<VaccineTypePeople, Integer>, CrudRepository<VaccineTypePeople, Integer> {

    /**
     * 依創建時間搜尋 VaccineTypePeople
     * @param createTime 創建時間 今日日期(YYYY-MM-DD)
     * @return VaccineTypePeople
     * */
    VaccineTypePeople findByCreateTime(String createTime);
}
