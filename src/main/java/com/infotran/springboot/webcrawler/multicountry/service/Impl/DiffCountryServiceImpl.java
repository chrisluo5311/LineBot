package com.infotran.springboot.webcrawler.multicountry.service.Impl;

import com.infotran.springboot.webcrawler.multicountry.dao.DiffCountryRepository;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.DiffCountryService;

import javax.annotation.Resource;

/**
 * DiffCountryService Implement
 * @author chris
 * */
public class DiffCountryServiceImpl implements DiffCountryService {

    @Resource
    DiffCountryRepository diffCountryRepository;

    @Override
    public DiffCountry save(DiffCountry diffCountry) {
        return diffCountryRepository.save(diffCountry);
    }



}
