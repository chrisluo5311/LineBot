package com.infotran.springboot.webcrawler.vaccinesvg.dao;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccineTypePeople;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface VaccinedTypePelpleRepo extends JpaRepository<VaccineTypePeople, Integer>, CrudRepository<VaccineTypePeople, Integer> {

    public VaccineTypePeople findByCreateTime(String createTime);
}
