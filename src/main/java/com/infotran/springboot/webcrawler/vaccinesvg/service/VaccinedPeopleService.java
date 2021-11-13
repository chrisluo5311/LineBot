package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import org.springframework.stereotype.Service;

/**
 * 各疫苗接踵累计人次service
 * @author chris
 */
@Service
public interface VaccinedPeopleService {

    /**
     * save VaccineTypePeople
     * @param vaccineTypePeople vaccineTypePeople
     * @return VaccineTypePeople
     * */
    VaccineTypePeople save(VaccineTypePeople vaccineTypePeople);

    /**
     * 依創建時間搜尋 VaccineTypePeople
     * @param createTime 創建時間
     * @return VaccineTypePeople
     * */
    VaccineTypePeople findByCreateTime(String createTime);

    /**
     * find One VaccineTypePeople
     * @return VaccineTypePeople
     * */
    VaccineTypePeople findOne();
}
