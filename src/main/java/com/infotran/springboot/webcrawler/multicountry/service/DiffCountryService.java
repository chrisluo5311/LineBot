package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;

import java.util.List;

public interface DiffCountryService {

    DiffCountry save(DiffCountry diffCountry);

    DiffCountry findByIsoCode(String isoCode);

    DiffCountry findByLastUpdate(String lastUpdate);

    List<DiffCountry> findAll();

    DiffCountry findByCountry(String country);

}
