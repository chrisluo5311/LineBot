package com.infotran.springboot.webcrawler.multicountry.service.Impl;

import com.infotran.springboot.webcrawler.multicountry.dao.DiffCountryRepository;
import com.infotran.springboot.webcrawler.multicountry.model.DiffCountry;
import com.infotran.springboot.webcrawler.multicountry.service.DiffCountryService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * DiffCountryService Implement
 * @author chris
 * */
@Service
public class DiffCountryServiceImpl implements DiffCountryService {

    @Resource
    DiffCountryRepository diffCountryRepository;

    @Override
    public DiffCountry save(DiffCountry diffCountry) {
        return diffCountryRepository.save(diffCountry);
    }

    @Override
    public DiffCountry findByIsoCode(@NonNull String isoCode) {
        return diffCountryRepository.findByIsoCode(isoCode);
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
