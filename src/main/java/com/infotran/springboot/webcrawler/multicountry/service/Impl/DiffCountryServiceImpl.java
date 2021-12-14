package com.infotran.springboot.webcrawler.multicountry.service.Impl;

import com.infotran.springboot.webcrawler.multicountry.dao.DiffCountryRepository;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.DiffCountryService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * DiffCountryService Implement
 * @author chris
 * */
@Slf4j
@Service
public class DiffCountryServiceImpl implements DiffCountryService {

    @Resource
    DiffCountryRepository diffCountryRepository;

    @Override
    public DiffCountry save(DiffCountry diffCountry) {
        return diffCountryRepository.save(diffCountry);
    }

    @Override
    public DiffCountry findByIsoCodeAndLastUpdate(String isoCode,String time) {
        DiffCountry diffCountry = diffCountryRepository.findDiffCountryByIsoCodeAndLastUpdate(isoCode,time);
        if(diffCountry!=null){
            return diffCountry;
        }
        return null;
    }

    @Override
    public DiffCountry findByLastUpdate(@NonNull String lastUpdate) {
        return diffCountryRepository.findByLastUpdate(lastUpdate);
    }

    @Override
    public List<DiffCountry> findAll() {
        return diffCountryRepository.findAll();
    }

    @Override
    public DiffCountry findByCountry(@NonNull String country) {
        Optional<DiffCountry> diffCountry = diffCountryRepository.findByCountry(country);
        return diffCountry.get();
    }

}
