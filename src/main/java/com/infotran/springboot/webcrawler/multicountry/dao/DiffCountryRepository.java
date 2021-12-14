package com.infotran.springboot.webcrawler.multicountry.dao;

import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * DiffCountryRepository
 * @author chris
 */
public interface DiffCountryRepository extends JpaRepository<DiffCountry, Integer> {

    DiffCountry findDiffCountryByIsoCodeAndLastUpdate(String isoCode,String time);

    DiffCountry findByLastUpdate(String lastUpdate);

    Optional<DiffCountry> findByCountry(String country);

}
