package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;

import java.util.List;

public interface DiffCountryService {

    DiffCountry save(DiffCountry diffCountry);

    DiffCountry findByIsoCodeAndLastUpdate(String isoCode,String time) ;

    DiffCountry findByLastUpdate(String lastUpdate);

    List<DiffCountry> findAll();

    DiffCountry findByCountry(String country);

}
