package com.infotran.springboot.webcrawler.vaccinesvg.service;

import com.infotran.springboot.webcrawler.vaccinesvg.model.VaccinedPDFRecord;
import org.springframework.stereotype.Service;

@Service
public interface CheckPDFRecordSevice {

    public String findByUploadTime(String uploadTime);

    public VaccinedPDFRecord save(VaccinedPDFRecord vaccinedPDFRecord);
}
