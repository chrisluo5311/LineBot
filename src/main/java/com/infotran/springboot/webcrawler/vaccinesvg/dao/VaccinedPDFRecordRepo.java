package com.infotran.springboot.webcrawler.vaccinesvg.dao;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface VaccinedPDFRecordRepo extends JpaRepository<VaccinedPDFRecord, Integer>, CrudRepository<VaccinedPDFRecord, Integer> {


    public VaccinedPDFRecord findByUploadTime(String uploadTime);
}
