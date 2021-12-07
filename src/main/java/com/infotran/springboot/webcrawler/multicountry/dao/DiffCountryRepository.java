package com.infotran.springboot.webcrawler.multicountry.dao;

import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DiffCountryRepository
 * @author chris
 */
public interface DiffCountryRepository extends JpaRepository<DiffCountry, Integer> {

    DiffCountry findByIsoCode(String isoCode);

    DiffCountry findByLastUpdate(String lastUpdate);

}
