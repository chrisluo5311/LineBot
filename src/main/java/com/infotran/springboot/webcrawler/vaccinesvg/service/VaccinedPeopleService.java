package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import org.springframework.stereotype.Service;

@Service
public interface VaccinedPeopleService {

    public VaccineTypePeople save(VaccineTypePeople vaccineTypePeople);

    public VaccineTypePeople findByCreateTime(String createTime);

    public VaccineTypePeople findAll();
}
