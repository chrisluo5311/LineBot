package com.infotran.springboot.webcrawler.multicountry.service;

import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;

public interface DiffCountryService {

    DiffCountry save(DiffCountry diffCountry);

    DiffCountry findByIsoCode(String isoCode);

    DiffCountry findByLastUpdate(String lastUpdate);


}
