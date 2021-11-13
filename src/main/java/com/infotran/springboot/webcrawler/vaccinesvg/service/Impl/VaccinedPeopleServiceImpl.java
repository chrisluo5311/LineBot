package com.infotran.springboot.webcrawler.vaccinesvg.service.Impl;

import com.infotran.springboot.webcrawler.vaccinesvg.dao.VaccinedTypePeopleRepo;
import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import com.infotran.springboot.webcrawler.vaccinesvg.service.VaccinedPeopleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * VaccinedPeopleService 實作類
 * @author chris
 */
@Service
@Slf4j
public class VaccinedPeopleServiceImpl implements VaccinedPeopleService {

    private static final String LOG_PREFIX = "VaccinedPeopleServiceImpl";

    @Resource
    VaccinedTypePeopleRepo vaccinedTypePeopleRepo;

    @Override
    public VaccineTypePeople save(VaccineTypePeople vaccineTypePeople) {
        String createTime = vaccineTypePeople.getCreateTime();
        //查詢是否有同一筆資料
        VaccineTypePeople vaccineTypePeople1 = findByCreateTime(createTime);
        if(Objects.nonNull(vaccineTypePeople1)){
            //同筆資料
            return vaccineTypePeople;
        }else {
            //不同筆
            vaccinedTypePeopleRepo.deleteAll();
        }
        return vaccinedTypePeopleRepo.save(vaccineTypePeople);
    }

    @Override
    public VaccineTypePeople findByCreateTime(String createTime) {
        VaccineTypePeople vaccineTypePeople = vaccinedTypePeopleRepo.findByCreateTime(createTime);
        return vaccineTypePeople!=null?vaccineTypePeople:null;
    }

    @Override
    public VaccineTypePeople findOne(){
        List<VaccineTypePeople> vPeople = vaccinedTypePeopleRepo.findAll();
        return vPeople.get(0);
    }
}
